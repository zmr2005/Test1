"""客户相关 Schema。"""
from datetime import datetime

from pydantic import BaseModel, ConfigDict


class CustomerCreate(BaseModel):
    name: str
    industry: str | None = None
    phone: str | None = None
    email: str | None = None
    status: str = "private"
    remark: str | None = None


class CustomerOut(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: int
    name: str
    industry: str | None = None
    phone: str | None = None
    email: str | None = None
    status: str
    owner_id: int | None = None
    last_follow_up_at: datetime | None = None
    created_at: datetime
    updated_at: datetime
