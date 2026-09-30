from pydantic import BaseModel
from pydantic_settings import BaseSettings, SettingsConfigDict


class ApiPrefix(BaseModel):
    user: str = "/user"
    auth: str = "/auth"
    v1_prefix: str = "/v1"


class ApiConf(BaseModel):
    # TODO you should remember about docker requests
    host: str = "0.0.0.0"
    port: int = 8001


class LogConf(BaseModel):
    format: str = (
        "[%(asctime)s.%(msecs)03d] %(module)10s:%(lineno)-3d %(levelname)-7s - %(message)s"
    )


class DatabaseConf(BaseModel):
    url: PostgresDsn
    echo: bool = False
    # echo: bool = True


class JWTConf(BaseModel):
    private_key_path: Path
    public_key_path: Path
    access_exp: int = 15
    refresh_exp: int = 30
    algorithm: str = "RS256"

    @property
    def private_key(self) -> str:
        return (BASE_DIR / self.private_key_path).read_text()

    @property
    def public_key(self) -> str:
        return (BASE_DIR / self.public_key_path).read_text()


class RateLimiterConf(BaseModel):
    login: str = "5/15minutes"


class MailConf(BaseModel):
    host: str = "sandbox.smtp.mailtrap.io"
    port: int = 2525
    start_tls: bool = True
    from_email: str = "noreply@torty-shop.com"
    username: str
    password: str


class Redis(BaseModel):
    url: RedisDsn


class Settings(BaseSettings):
    model_config = SettingsConfigDict(
        env_file=(".env.template", ".env"),
        case_sensitive=False,
        env_nested_delimiter="__",
        frozen=True,
        extra="forbid",
    )

    api_prefix: ApiPrefix = ApiPrefix()
    api_conf: ApiConf = ApiConf()
    logging_conf: LogConf = LogConf()
    db: DatabaseConf
    jwt: JWTConf
    limiter: RateLimiterConf = RateLimiterConf()
    mail: MailConf
    redis: Redis


settings = Settings()
