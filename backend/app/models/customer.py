"""客户与联系人模型。"""
from datetime import datetime

from sqlalchemy import DateTime, ForeignKey, String, Text
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.core.database import Base
from app.models.base import TimestampMixin

# 客户归属状态：私有(跟进中) / 公海(可认领) / 锁定
CUSTOMER_STATUS = ("private", "public", "locked")


class Customer(Base, TimestampMixin):
    __tablename__ = "customers"

    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    name: Mapped[str] = mapped_column(String(128), nullable=False)
    industry: Mapped[str | None] = mapped_column(String(64))
    phone: Mapped[str | None] = mapped_column(String(32), index=True)
    email: Mapped[str | None] = mapped_column(String(128))
    status: Mapped[str] = mapped_column(String(16), default="private", index=True)
    owner_id: Mapped[int | None] = mapped_column(index=True)
    last_follow_up_at: Mapped[datetime | None] = mapped_column(DateTime, comment="最近跟进时间")
    remark: Mapped[str | None] = mapped_column(Text)

    contacts: Mapped[list["Contact"]] = relationship(
        back_populates="customer", cascade="all, delete-orphan"
    )


class Contact(Base, TimestampMixin):
    __tablename__ = "contacts"

    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    customer_id: Mapped[int] = mapped_column(ForeignKey("customers.id"), index=True)
    name: Mapped[str] = mapped_column(String(64), nullable=False)
    position: Mapped[str | None] = mapped_column(String(64))
    phone: Mapped[str | None] = mapped_column(String(32))
    email: Mapped[str | None] = mapped_column(String(128))

    customer: Mapped["Customer"] = relationship(back_populates="contacts")
