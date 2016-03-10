/**
 * Autogenerated by Thrift Compiler (0.9.3)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package dsn.replication;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.server.AbstractNonblockingServer.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import javax.annotation.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked"})
@Generated(value = "Autogenerated by Thrift Compiler (0.9.3)", date = "2016-03-09")
public class query_cfg_response implements org.apache.thrift.TBase<query_cfg_response, query_cfg_response._Fields>, java.io.Serializable, Cloneable, Comparable<query_cfg_response> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("query_cfg_response");

  private static final org.apache.thrift.protocol.TField ERR_FIELD_DESC = new org.apache.thrift.protocol.TField("err", org.apache.thrift.protocol.TType.STRUCT, (short)1);
  private static final org.apache.thrift.protocol.TField APP_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("app_id", org.apache.thrift.protocol.TType.I32, (short)2);
  private static final org.apache.thrift.protocol.TField PARTITION_COUNT_FIELD_DESC = new org.apache.thrift.protocol.TField("partition_count", org.apache.thrift.protocol.TType.I32, (short)3);
  private static final org.apache.thrift.protocol.TField PARTITIONS_FIELD_DESC = new org.apache.thrift.protocol.TField("partitions", org.apache.thrift.protocol.TType.LIST, (short)4);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new query_cfg_responseStandardSchemeFactory());
    schemes.put(TupleScheme.class, new query_cfg_responseTupleSchemeFactory());
  }

  public dsn.base.error_code err; // required
  public int app_id; // required
  public int partition_count; // required
  public List<partition_configuration> partitions; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    ERR((short)1, "err"),
    APP_ID((short)2, "app_id"),
    PARTITION_COUNT((short)3, "partition_count"),
    PARTITIONS((short)4, "partitions");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // ERR
          return ERR;
        case 2: // APP_ID
          return APP_ID;
        case 3: // PARTITION_COUNT
          return PARTITION_COUNT;
        case 4: // PARTITIONS
          return PARTITIONS;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __APP_ID_ISSET_ID = 0;
  private static final int __PARTITION_COUNT_ISSET_ID = 1;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.ERR, new org.apache.thrift.meta_data.FieldMetaData("err", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, dsn.base.error_code.class)));
    tmpMap.put(_Fields.APP_ID, new org.apache.thrift.meta_data.FieldMetaData("app_id", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.PARTITION_COUNT, new org.apache.thrift.meta_data.FieldMetaData("partition_count", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.PARTITIONS, new org.apache.thrift.meta_data.FieldMetaData("partitions", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, partition_configuration.class))));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(query_cfg_response.class, metaDataMap);
  }

  public query_cfg_response() {
  }

  public query_cfg_response(
    dsn.base.error_code err,
    int app_id,
    int partition_count,
    List<partition_configuration> partitions)
  {
    this();
    this.err = err;
    this.app_id = app_id;
    setApp_idIsSet(true);
    this.partition_count = partition_count;
    setPartition_countIsSet(true);
    this.partitions = partitions;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public query_cfg_response(query_cfg_response other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetErr()) {
      this.err = new dsn.base.error_code(other.err);
    }
    this.app_id = other.app_id;
    this.partition_count = other.partition_count;
    if (other.isSetPartitions()) {
      List<partition_configuration> __this__partitions = new ArrayList<partition_configuration>(other.partitions.size());
      for (partition_configuration other_element : other.partitions) {
        __this__partitions.add(new partition_configuration(other_element));
      }
      this.partitions = __this__partitions;
    }
  }

  public query_cfg_response deepCopy() {
    return new query_cfg_response(this);
  }

  @Override
  public void clear() {
    this.err = null;
    setApp_idIsSet(false);
    this.app_id = 0;
    setPartition_countIsSet(false);
    this.partition_count = 0;
    this.partitions = null;
  }

  public dsn.base.error_code getErr() {
    return this.err;
  }

  public query_cfg_response setErr(dsn.base.error_code err) {
    this.err = err;
    return this;
  }

  public void unsetErr() {
    this.err = null;
  }

  /** Returns true if field err is set (has been assigned a value) and false otherwise */
  public boolean isSetErr() {
    return this.err != null;
  }

  public void setErrIsSet(boolean value) {
    if (!value) {
      this.err = null;
    }
  }

  public int getApp_id() {
    return this.app_id;
  }

  public query_cfg_response setApp_id(int app_id) {
    this.app_id = app_id;
    setApp_idIsSet(true);
    return this;
  }

  public void unsetApp_id() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __APP_ID_ISSET_ID);
  }

  /** Returns true if field app_id is set (has been assigned a value) and false otherwise */
  public boolean isSetApp_id() {
    return EncodingUtils.testBit(__isset_bitfield, __APP_ID_ISSET_ID);
  }

  public void setApp_idIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __APP_ID_ISSET_ID, value);
  }

  public int getPartition_count() {
    return this.partition_count;
  }

  public query_cfg_response setPartition_count(int partition_count) {
    this.partition_count = partition_count;
    setPartition_countIsSet(true);
    return this;
  }

  public void unsetPartition_count() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __PARTITION_COUNT_ISSET_ID);
  }

  /** Returns true if field partition_count is set (has been assigned a value) and false otherwise */
  public boolean isSetPartition_count() {
    return EncodingUtils.testBit(__isset_bitfield, __PARTITION_COUNT_ISSET_ID);
  }

  public void setPartition_countIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __PARTITION_COUNT_ISSET_ID, value);
  }

  public int getPartitionsSize() {
    return (this.partitions == null) ? 0 : this.partitions.size();
  }

  public java.util.Iterator<partition_configuration> getPartitionsIterator() {
    return (this.partitions == null) ? null : this.partitions.iterator();
  }

  public void addToPartitions(partition_configuration elem) {
    if (this.partitions == null) {
      this.partitions = new ArrayList<partition_configuration>();
    }
    this.partitions.add(elem);
  }

  public List<partition_configuration> getPartitions() {
    return this.partitions;
  }

  public query_cfg_response setPartitions(List<partition_configuration> partitions) {
    this.partitions = partitions;
    return this;
  }

  public void unsetPartitions() {
    this.partitions = null;
  }

  /** Returns true if field partitions is set (has been assigned a value) and false otherwise */
  public boolean isSetPartitions() {
    return this.partitions != null;
  }

  public void setPartitionsIsSet(boolean value) {
    if (!value) {
      this.partitions = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case ERR:
      if (value == null) {
        unsetErr();
      } else {
        setErr((dsn.base.error_code)value);
      }
      break;

    case APP_ID:
      if (value == null) {
        unsetApp_id();
      } else {
        setApp_id((Integer)value);
      }
      break;

    case PARTITION_COUNT:
      if (value == null) {
        unsetPartition_count();
      } else {
        setPartition_count((Integer)value);
      }
      break;

    case PARTITIONS:
      if (value == null) {
        unsetPartitions();
      } else {
        setPartitions((List<partition_configuration>)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case ERR:
      return getErr();

    case APP_ID:
      return getApp_id();

    case PARTITION_COUNT:
      return getPartition_count();

    case PARTITIONS:
      return getPartitions();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case ERR:
      return isSetErr();
    case APP_ID:
      return isSetApp_id();
    case PARTITION_COUNT:
      return isSetPartition_count();
    case PARTITIONS:
      return isSetPartitions();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof query_cfg_response)
      return this.equals((query_cfg_response)that);
    return false;
  }

  public boolean equals(query_cfg_response that) {
    if (that == null)
      return false;

    boolean this_present_err = true && this.isSetErr();
    boolean that_present_err = true && that.isSetErr();
    if (this_present_err || that_present_err) {
      if (!(this_present_err && that_present_err))
        return false;
      if (!this.err.equals(that.err))
        return false;
    }

    boolean this_present_app_id = true;
    boolean that_present_app_id = true;
    if (this_present_app_id || that_present_app_id) {
      if (!(this_present_app_id && that_present_app_id))
        return false;
      if (this.app_id != that.app_id)
        return false;
    }

    boolean this_present_partition_count = true;
    boolean that_present_partition_count = true;
    if (this_present_partition_count || that_present_partition_count) {
      if (!(this_present_partition_count && that_present_partition_count))
        return false;
      if (this.partition_count != that.partition_count)
        return false;
    }

    boolean this_present_partitions = true && this.isSetPartitions();
    boolean that_present_partitions = true && that.isSetPartitions();
    if (this_present_partitions || that_present_partitions) {
      if (!(this_present_partitions && that_present_partitions))
        return false;
      if (!this.partitions.equals(that.partitions))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_err = true && (isSetErr());
    list.add(present_err);
    if (present_err)
      list.add(err);

    boolean present_app_id = true;
    list.add(present_app_id);
    if (present_app_id)
      list.add(app_id);

    boolean present_partition_count = true;
    list.add(present_partition_count);
    if (present_partition_count)
      list.add(partition_count);

    boolean present_partitions = true && (isSetPartitions());
    list.add(present_partitions);
    if (present_partitions)
      list.add(partitions);

    return list.hashCode();
  }

  @Override
  public int compareTo(query_cfg_response other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetErr()).compareTo(other.isSetErr());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetErr()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.err, other.err);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetApp_id()).compareTo(other.isSetApp_id());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetApp_id()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.app_id, other.app_id);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetPartition_count()).compareTo(other.isSetPartition_count());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetPartition_count()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.partition_count, other.partition_count);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetPartitions()).compareTo(other.isSetPartitions());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetPartitions()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.partitions, other.partitions);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("query_cfg_response(");
    boolean first = true;

    sb.append("err:");
    if (this.err == null) {
      sb.append("null");
    } else {
      sb.append(this.err);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("app_id:");
    sb.append(this.app_id);
    first = false;
    if (!first) sb.append(", ");
    sb.append("partition_count:");
    sb.append(this.partition_count);
    first = false;
    if (!first) sb.append(", ");
    sb.append("partitions:");
    if (this.partitions == null) {
      sb.append("null");
    } else {
      sb.append(this.partitions);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
    if (err != null) {
      err.validate();
    }
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class query_cfg_responseStandardSchemeFactory implements SchemeFactory {
    public query_cfg_responseStandardScheme getScheme() {
      return new query_cfg_responseStandardScheme();
    }
  }

  private static class query_cfg_responseStandardScheme extends StandardScheme<query_cfg_response> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, query_cfg_response struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // ERR
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.err = new dsn.base.error_code();
              struct.err.read(iprot);
              struct.setErrIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // APP_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.app_id = iprot.readI32();
              struct.setApp_idIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // PARTITION_COUNT
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.partition_count = iprot.readI32();
              struct.setPartition_countIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // PARTITIONS
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list24 = iprot.readListBegin();
                struct.partitions = new ArrayList<partition_configuration>(_list24.size);
                partition_configuration _elem25;
                for (int _i26 = 0; _i26 < _list24.size; ++_i26)
                {
                  _elem25 = new partition_configuration();
                  _elem25.read(iprot);
                  struct.partitions.add(_elem25);
                }
                iprot.readListEnd();
              }
              struct.setPartitionsIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, query_cfg_response struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.err != null) {
        oprot.writeFieldBegin(ERR_FIELD_DESC);
        struct.err.write(oprot);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(APP_ID_FIELD_DESC);
      oprot.writeI32(struct.app_id);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(PARTITION_COUNT_FIELD_DESC);
      oprot.writeI32(struct.partition_count);
      oprot.writeFieldEnd();
      if (struct.partitions != null) {
        oprot.writeFieldBegin(PARTITIONS_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, struct.partitions.size()));
          for (partition_configuration _iter27 : struct.partitions)
          {
            _iter27.write(oprot);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class query_cfg_responseTupleSchemeFactory implements SchemeFactory {
    public query_cfg_responseTupleScheme getScheme() {
      return new query_cfg_responseTupleScheme();
    }
  }

  private static class query_cfg_responseTupleScheme extends TupleScheme<query_cfg_response> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, query_cfg_response struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetErr()) {
        optionals.set(0);
      }
      if (struct.isSetApp_id()) {
        optionals.set(1);
      }
      if (struct.isSetPartition_count()) {
        optionals.set(2);
      }
      if (struct.isSetPartitions()) {
        optionals.set(3);
      }
      oprot.writeBitSet(optionals, 4);
      if (struct.isSetErr()) {
        struct.err.write(oprot);
      }
      if (struct.isSetApp_id()) {
        oprot.writeI32(struct.app_id);
      }
      if (struct.isSetPartition_count()) {
        oprot.writeI32(struct.partition_count);
      }
      if (struct.isSetPartitions()) {
        {
          oprot.writeI32(struct.partitions.size());
          for (partition_configuration _iter28 : struct.partitions)
          {
            _iter28.write(oprot);
          }
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, query_cfg_response struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(4);
      if (incoming.get(0)) {
        struct.err = new dsn.base.error_code();
        struct.err.read(iprot);
        struct.setErrIsSet(true);
      }
      if (incoming.get(1)) {
        struct.app_id = iprot.readI32();
        struct.setApp_idIsSet(true);
      }
      if (incoming.get(2)) {
        struct.partition_count = iprot.readI32();
        struct.setPartition_countIsSet(true);
      }
      if (incoming.get(3)) {
        {
          org.apache.thrift.protocol.TList _list29 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
          struct.partitions = new ArrayList<partition_configuration>(_list29.size);
          partition_configuration _elem30;
          for (int _i31 = 0; _i31 < _list29.size; ++_i31)
          {
            _elem30 = new partition_configuration();
            _elem30.read(iprot);
            struct.partitions.add(_elem30);
          }
        }
        struct.setPartitionsIsSet(true);
      }
    }
  }

}
