import uuid

from fastapi import APIRouter, Response

from app.api.deps import SessionDep

router = APIRouter(prefix="/products", tags=["products"])


@router.post("/", summary="Create a new skin care product in the user's inventory")
async def create_product(*, session: SessionDep) -> Response:
    raise NotImplementedError


@router.get("/", summary="Get all skin care products in the user's inventory")
async def get_products(*, session: SessionDep) -> Response:
    raise NotImplementedError


@router.put("/{product_id}", summary="Update a specific skin care product")
async def update_product(product_id: uuid.UUID, *, session: SessionDep) -> Response:
    raise NotImplementedError


@router.delete("/{product_id}", summary="Delete a specific skin care product")
async def delete_product(product_id: uuid.UUID, *, session: SessionDep) -> Response:
    raise NotImplementedError
