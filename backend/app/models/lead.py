"""线索模型。"""
from sqlalchemy import String, Text
from sqlalchemy.orm import Mapped, mapped_column

from app.models.base import TimestampMixin
from app.core.database import Base

# 线索状态：待分配 / 已分配 / 已转化 / 已作废 / 回收站
LEAD_STATUS = ("pending", "assigned", "converted", "invalid", "recycled")


class Lead(Base, TimestampMixin):
    __tablename__ = "leads"

    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    name: Mapped[str] = mapped_column(String(64), nullable=False)
    company: Mapped[str | None] = mapped_column(String(128))
    phone: Mapped[str | None] = mapped_column(String(32), index=True)
    email: Mapped[str | None] = mapped_column(String(128), index=True)
    source: Mapped[str | None] = mapped_column(String(32), comment="来源渠道")
    tags: Mapped[str | None] = mapped_column(String(255), comment="逗号分隔标签")
    status: Mapped[str] = mapped_column(String(16), default="pending", index=True)
    owner_id: Mapped[int | None] = mapped_column(index=True, comment="负责人用户ID")
    remark: Mapped[str | None] = mapped_column(Text)
