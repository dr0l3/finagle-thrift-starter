namespace java com.example.thrift.generated
#@namespace scala com.example.thrift.generated

enum Sex {
    MALE,
    FEMALE,
    OTHER
}

struct User {
    1: string id
    2: string name
    3: i16 age
}

struct Fairy {
    1: string id
    2: string name
    3: i16 age
}

service UserService {
  User getUser(1: string id)
}

service BinaryService {
  string fetchBlob(1: i64 id)
}

service FairyService {
  Fairy getFairy(1: string id)
}

service TestService {
  string hello(1: string name)
  string loadTest(1: i64 round)
}


service Service2 {
  string hello(1: string id)
  string hello2(1: string id)
}

service Service3 {
  string world(1: string id)
  string world2(1: string id)
}