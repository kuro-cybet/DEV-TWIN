
from app.services import UserService

def run():
    svc = UserService()
    return svc.get_user()
