# Custom exceptions to catch them automatically
# and isolate the core layer because it is the lowest layer


class InvalidTokenTypeError(Exception):
    # its the best thing i have ever made for the last time
    def __init__(self, message: str = "This token type does not match"):
        super().__init__(message)


class TokenValidationError(Exception):
    def __init__(self, message: str = "Invalid token"):
        super().__init__(message)


class IntegrityConstraintError(Exception):
    def __init__(self, message: str = "Email already exists"):
        super().__init__(message)


class DatabaseError(Exception):
    def __init__(self, message: str = "Database failure"):
        super().__init__(message)


class InvalidCredentialsError(Exception):
    def __init__(self, message: str = "Invalid credentials"):
        super().__init__(message)


class UserBlockedError(Exception):
    def __init__(self, message: str = "User blocked"):
        super().__init__(message)


class InvalidCodeError(Exception):
    def __init__(self, message: str = "Invalid or expired confirmation code"):
        super().__init__(message)
