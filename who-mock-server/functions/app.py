import json
 
lita = aUser("lita", "student")
bema = aUser("bema", "teacher")
bozy = aUser("bozy", "teacher")
lou = aUser("lou", "manager")

forbiddenResponse = {
    "statusCode": 400,
    "body": "forbidden"
}

def whoami_handler(event, context):
    headers = event["headers"]
    bearer =  headers["authorization"]
    if bearer == lita["bearer"]:
        return aUserResponse(200, lita)
    if bearer == bema["bearer"]:
        return aUserResponse(200, bema)
    if bearer == bozy["bearer"]:
        return aUserResponse(200, bozy)
    if bearer == lou["bearer"]:
        return aUserResponse(200, lou)
    return forbiddenResponse

def aUser(name, role):
    return {
        "id": name + "Id",
        "bearer": "Bearer " + name,
        "role": role
    }

def aUserResponse(statusCode, user):
    return {
        "statusCode": statusCode,
        "body": json.dumps({
            "id": user["id"],
            "role": user["role"]
        })
    }