from typing import Union
from typing import Annotated
from fastapi import FastAPI, Depends, Security

from fastapi_keycloak.keycloak import get_current_user, verify_token, oauth2_scheme

app = FastAPI()


@app.get("/")
def read_root():
    return {"Hello": "World"}

@app.get("/users/me")
def read_users_me(token: str = Security(oauth2_scheme)):
    user = get_current_user(token)
    return user

@app.get("/test")
def secure_read_test(payload: dict = Security(verify_token)):
    return {"message": "This is a secure endpoint", "user": payload["preferred_username"]}

@app.get("/items/{item_id}")
def read_item(item_id: int, q: Union[str, None] = None, payload: dict = Depends(verify_token)):
    return {"item_id": item_id, "q": q}

@app.get("/secure-data")
def get_secure_data(payload: dict = Depends(verify_token)):
    return {"message": "This is a secure endpoint", "user": payload["preferred_username"]}