
include "dsn.thrift"
include "replication.thrift"

namespace cpp dsn.apps
namespace java dsn.apps
namespace py dsn.apps

struct update_request
{
    1:dsn.blob      key;
    2:dsn.blob      value;
}

struct read_response
{
    1:i32      error;
    2:dsn.blob   value;
}

struct replication_read_response
{
    1:dsn.error_code ec;
    2:optional read_response app_response;
}

struct replication_write_response
{
    1:dsn.error_code ec;
    2:optional i32 app_response;
}

service rrdb
{
    replication_write_response put(1:replication.write_request_header header, 2:update_request update);
    replication_write_response remove(1:replication.write_request_header header, 2:dsn.blob key);
    replication_write_response merge(1:replication.write_request_header header, 2:update_request update);
    replication_read_response get(1:replication.read_request_header header, 2:dsn.blob key);
}

service meta
{
    replication.query_cfg_response query_cfg(1:replication.query_cfg_request query);
}

