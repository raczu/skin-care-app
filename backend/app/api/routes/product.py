import uuid
from typing import Annotated

from fastapi import APIRouter, Query, Response, status

from app import crud
from app.api.deps import CurrentUserDep, SessionDep
from app.api.utils import get_owned_resource_or_404, paginate
from app.schemas import PaginatedResponse, PaginationParams, Product, ProductCreate, ProductUpdate

router = APIRouter(prefix="/products", tags=["products"])

PRODUCT_NOT_FOUND_MESSAGE = "Product not found or you don't have access to it"


@router.post(
    "/",
    summary="Create a new skin care product in the user's inventory",
    status_code=status.HTTP_201_CREATED,
    response_model=Product,
)
async def create_product(
    *, product_in: ProductCreate, session: SessionDep, user: CurrentUserDep
) -> Product:
    product = await crud.product.create_product(session, user.id, product_in)
    return product


@router.get(
    "/",
    summary="Get all skin care products in the user's inventory",
    response_model=PaginatedResponse[Product],
)
async def get_products(
    params: Annotated[PaginationParams, Query()],
    *,
    session: SessionDep,
    user: CurrentUserDep,
) -> PaginatedResponse[Product]:
    products, total = await crud.product.get_user_products(session, user.id, params)
    return paginate(items=products, total=total, limit=params.limit, offset=params.offset)


@router.get("/{id}", summary="Get a specific skin care product", response_model=Product)
async def get_product_by_id(id: uuid.UUID, *, session: SessionDep, user: CurrentUserDep) -> Product:
    product = await get_owned_resource_or_404(
        session=session,
        getter=crud.product.get_product_by_id,
        resource_id=id,
        owner_id=user.id,
        detail=PRODUCT_NOT_FOUND_MESSAGE,
    )
    return product


@router.patch("/{id}", summary="Update a specific skin care product", response_model=Product)
async def update_product(
    id: uuid.UUID, *, product_in: ProductUpdate, session: SessionDep, user: CurrentUserDep
) -> Product:
    product = await get_owned_resource_or_404(
        session=session,
        getter=crud.product.get_product_by_id,
        resource_id=id,
        owner_id=user.id,
        detail=PRODUCT_NOT_FOUND_MESSAGE,
    )
    product = await crud.product.update_product(session, product, product_in)
    return product


@router.delete(
    "/{id}", summary="Delete a specific skin care product", status_code=status.HTTP_204_NO_CONTENT
)
async def delete_product(id: uuid.UUID, *, session: SessionDep, user: CurrentUserDep) -> Response:
    product = await get_owned_resource_or_404(
        session=session,
        getter=crud.product.get_product_by_id,
        resource_id=id,
        owner_id=user.id,
        detail=PRODUCT_NOT_FOUND_MESSAGE,
    )
    await crud.product.delete_product(session, product)
    return Response(status_code=status.HTTP_204_NO_CONTENT)
