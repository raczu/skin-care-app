import uuid

from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from app.crud.utils import get_paginated_resources
from app.database.models import Product
from app.schemas import PaginationParams, ProductCreate, ProductUpdate


async def get_user_products(
    session: AsyncSession, user_id: uuid.UUID, pagination: PaginationParams | None = None
) -> tuple[list[Product], int]:
    """Retrieve products for a specific user with pagination.

    Returns:
        A tuple containing a list of Product instances and the total count of products.
    """
    if pagination is None:
        pagination = PaginationParams()
    statement = (
        select(Product).where(Product.user_id == user_id).order_by(Product.updated_at.desc())
    )
    products, total = await get_paginated_resources(
        session, statement, limit=pagination.limit, offset=pagination.offset
    )
    return products, total


async def get_product_by_id(session: AsyncSession, id: uuid.UUID) -> Product | None:
    product = await session.get(Product, id)
    return product


async def get_user_products_by_ids(
    session: AsyncSession, user_id: uuid.UUID, ids: list[uuid.UUID]
) -> list[Product]:
    statement = select(Product).where(
        Product.user_id == user_id,
        Product.id.in_(ids),
    )
    result = await session.execute(statement)
    products = result.scalars().all()
    return list(products)


async def create_product(
    session: AsyncSession, user_id: uuid.UUID, product_in: ProductCreate
) -> Product:
    product = Product(
        user_id=user_id,
        name=product_in.name,
        brand=product_in.brand,
        purpose=product_in.purpose,
        description=product_in.description,
    )
    session.add(product)
    await session.flush()
    await session.refresh(product)
    return product


async def update_product(
    session: AsyncSession, product: Product, product_in: ProductUpdate
) -> Product:
    product.name = product_in.name or product.name
    product.brand = product_in.brand or product.brand
    product.purpose = product_in.purpose or product.purpose
    product.description = product_in.description or product.description
    await session.flush()
    await session.refresh(product)
    return product


async def delete_product(session: AsyncSession, product: Product) -> None:
    await session.delete(product)
    await session.flush()
