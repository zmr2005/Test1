"""商机模型。"""
from datetime import date

from sqlalchemy import Date, ForeignKey, Numeric, String, Text
from sqlalchemy.orm import Mapped, mapped_column

from app.core.database import Base
from app.models.base import TimestampMixin

# 商机阶段：初步接触 / 报价 / 谈判 / 成交 / 失败
OPPORTUNITY_STAGE = ("contact", "quotation", "negotiation", "won", "lost")


class Opportunity(Base, TimestampMixin):
    __tablename__ = "opportunities"

    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    customer_id: Mapped[int] = mapped_column(ForeignKey("customers.id"), index=True)
    name: Mapped[str] = mapped_column(String(128), nullable=False, comment="商机名称")
    product: Mapped[str | None] = mapped_column(String(128), comment="意向产品")
    budget: Mapped[float | None] = mapped_column(Numeric(12, 2), comment="预算")
    stage: Mapped[str] = mapped_column(String(16), default="contact", index=True)
    expected_close_date: Mapped[date | None] = mapped_column(Date, comment="预计成交日期")
    owner_id: Mapped[int | None] = mapped_column(index=True)
    remark: Mapped[str | None] = mapped_column(Text)
