# Custom exceptions to catch them automatically
# and isolate the core layer because it is the lowest layer


class InvalidTokenTypeError(Exception):
    pass


class TokenInvalidError(Exception):
    pass
