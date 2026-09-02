"""密码哈希与 JWT 工具（零额外依赖：pbkdf2 + PyJWT）。"""
import hashlib
import secrets
from datetime import datetime, timedelta, timezone

import jwt

from app.core.config import settings

ALGORITHM = "HS256"
_ITERATIONS = 100_000


def hash_password(password: str) -> str:
    salt = secrets.token_hex(16)
    dk = hashlib.pbkdf2_hmac("sha256", password.encode(), salt.encode(), _ITERATIONS)
    return f"{salt}${dk.hex()}"


def verify_password(plain: str, hashed: str) -> bool:
    try:
        salt, dk_hex = hashed.split("$", 1)
    except ValueError:
        return False
    dk = hashlib.pbkdf2_hmac("sha256", plain.encode(), salt.encode(), _ITERATIONS)
    return secrets.compare_digest(dk.hex(), dk_hex)


def create_access_token(subject: str, expires_minutes: int | None = None) -> str:
    minutes = expires_minutes or settings.ACCESS_TOKEN_EXPIRE_MINUTES
    expire = datetime.now(timezone.utc) + timedelta(minutes=minutes)
    payload = {"sub": subject, "exp": expire}
    return jwt.encode(payload, settings.SECRET_KEY, algorithm=ALGORITHM)


def decode_access_token(token: str) -> dict:
    return jwt.decode(token, settings.SECRET_KEY, algorithms=[ALGORITHM])
