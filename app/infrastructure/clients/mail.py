import time
from email.message import EmailMessage

import aiosmtplib
from core import settings


class MailService:
    async def send_confirmation_code_through_email(
        self, to_email: str, code: str
    ) -> None:
        message = EmailMessage()  # useful object to avoid writing this manually
        message["From"] = settings.mail.from_email
        message["To"] = to_email
        message["Subject"] = "Код подтверждения регистрации"
        message.set_content(f"Ваш код подтверждения: {code}\n\nОн действует 5 минут.")

        t0 = time.monotonic()
        await aiosmtplib.send(
            message,
            hostname=settings.mail.host,
            port=settings.mail.port,
            username=settings.mail.username,
            password=settings.mail.password,
            start_tls=settings.mail.start_tls,
            timeout=10,
        )
        print(f"send: {time.monotonic() - t0:.2f}s")


mail_service = MailService()
