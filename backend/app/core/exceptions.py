from fastapi import HTTPException, Request, status
from fastapi.exceptions import RequestValidationError, ValidationException
from fastapi.utils import is_body_allowed_for_status_code
from starlette.responses import JSONResponse, Response


class RequirementMismatchError(ValidationException):
    """Raised when an entity update violates domain-specific requirements."""


STATUS_TITLES = {
    status.HTTP_400_BAD_REQUEST: "Bad Request",
    status.HTTP_401_UNAUTHORIZED: "Unauthorized",
    status.HTTP_403_FORBIDDEN: "Forbidden",
    status.HTTP_404_NOT_FOUND: "Not Found",
    status.HTTP_405_METHOD_NOT_ALLOWED: "Method Not Allowed",
    status.HTTP_409_CONFLICT: "Conflict",
    status.HTTP_422_UNPROCESSABLE_ENTITY: "Unprocessable Entity",
    status.HTTP_429_TOO_MANY_REQUESTS: "Too Many Requests",
    status.HTTP_500_INTERNAL_SERVER_ERROR: "Internal Server Error",
}


async def http_exception_handler(request: Request, exc: HTTPException) -> Response:
    headers = dict(getattr(exc, "headers", None) or {})
    if not is_body_allowed_for_status_code(exc.status_code):
        return Response(status_code=exc.status_code, headers=headers)
    headers.setdefault("Content-Type", "application/problem+json")
    return JSONResponse(
        status_code=exc.status_code,
        content={
            "title": STATUS_TITLES.get(exc.status_code, "Error"),
            "status": exc.status_code,
            "detail": exc.detail,
            "instance": str(request.url.path),
        },
        headers=headers,
    )


async def validation_exception_handler(
    request: Request, exc: RequestValidationError | RequirementMismatchError
) -> JSONResponse:
    detail = (
        "One or more fields in the request body or parameters are invalid"
        if isinstance(exc, RequestValidationError)
        else "One or more fields affected entity constraints after update"
    )
    return JSONResponse(
        status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
        content={
            "title": "Your request parameters didn't validate",
            "status": status.HTTP_422_UNPROCESSABLE_ENTITY,
            "detail": detail,
            "instance": str(request.url.path),
            "errors": [
                {"field": ".".join([str(loc) for loc in err["loc"][1:]]), "message": err["msg"]}
                for err in exc.errors()
            ],
        },
        headers={"Content-Type": "application/problem+json"},
    )
