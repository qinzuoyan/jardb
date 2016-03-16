include "dsn.thrift"

namespace cpp dsn.replication
namespace java dsn.replication
namespace py dsn.replication

enum read_semantic
{
    ReadInvalid,
    ReadLastUpdate,
    ReadOutdated,
    ReadSnapshot,
}

struct global_partition_id
{
    1:i32  app_id = -1;
    2:i32  pidx = -1;
}

struct read_request_header
{
    1:global_partition_id gpid;
    2:dsn.task_code code;
    3:read_semantic semantic = read_semantic.ReadLastUpdate;
    4:i64   version_decree = -1;
}

struct write_request_header
{
    1:global_partition_id gpid;
    2:dsn.task_code code;
}

struct partition_configuration
{
    1:string                 app_type;
    2:global_partition_id    gpid;
    3:i64                    ballot;
    4:i32                    max_replica_count;
    5:dsn.rpc_address        primary;
    6:list<dsn.rpc_address>  secondaries;
    7:list<dsn.rpc_address>  last_drops;
    8:i64                    last_committed_decree;
}

struct query_cfg_request
{
    1:string    app_name;
    2:list<i32> partition_indices;
}

struct query_cfg_response
{
    1:dsn.error_code                err;
    2:i32                           app_id;
    3:i32                           partition_count;
    4:list<partition_configuration> partitions;
}
