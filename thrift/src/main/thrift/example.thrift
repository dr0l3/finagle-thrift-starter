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