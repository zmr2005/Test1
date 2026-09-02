"""通用 Schema。"""
from typing import Generic, TypeVar

from pydantic import BaseModel

T = TypeVar("T")


class Page(BaseModel, Generic[T]):
    """统一分页响应。"""

    items: list[T]
    total: int
