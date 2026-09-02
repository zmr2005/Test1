"""FastAPI 依赖注入。"""
from typing import Annotated

from fastapi import Depends
from sqlalchemy.orm import Session

from app.core.database import get_db

DbSession = Annotated[Session, Depends(get_db)]


def get_current_user() -> int:
    """返回当前登录用户 ID。

    TODO: 接入用户认证模块后，从请求头的 JWT 中解析真实用户 ID。
    目前脚手架阶段先返回占位值，便于各接口开发调试。
    """
    return 1


CurrentUser = Annotated[int, Depends(get_current_user)]
