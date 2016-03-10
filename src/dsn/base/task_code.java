/**
 * Autogenerated by Thrift Compiler (0.9.3)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package dsn.base;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;
import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;

import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Collections;

import javax.annotation.Generated;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked"})
@Generated(value = "Autogenerated by Thrift Compiler (0.9.3)", date = "2016-03-09")
public class task_code implements org.apache.thrift.TBase<task_code, task_code._Fields>, java.io.Serializable, Cloneable, Comparable<task_code> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("task_code");


  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new task_codeStandardSchemeFactory());
    schemes.put(TupleScheme.class, new task_codeTupleSchemeFactory());
  }


  public String task_message;
  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
;

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
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(task_code.class, metaDataMap);
  }

  public task_code() {
  }

  public task_code(String message) {
	this.task_message = message;
  }
  /**
   * Performs a deep copy on <i>other</i>.
   */
  public task_code(task_code other) {
	task_message = other.task_message;
  }

  public task_code deepCopy() {
    return new task_code(this);
  }

  @Override
  public void clear() {
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof task_code)
      return this.equals((task_code)that);
    return false;
  }

  public boolean equals(task_code that) {
    if (that == null)
      return false;
    return task_message.equals(that.task_message);
  }

  @Override
  public int hashCode() {
	return task_message.hashCode();
  }

  @Override
  public int compareTo(task_code other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }
    return task_message.compareTo(other.task_message);
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
    StringBuilder sb = new StringBuilder("task_code(");
    sb.append(task_message);
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
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
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class task_codeStandardSchemeFactory implements SchemeFactory {
    public task_codeStandardScheme getScheme() {
      return new task_codeStandardScheme();
    }
  }

  private static class task_codeStandardScheme extends StandardScheme<task_code> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, task_code struct) throws org.apache.thrift.TException {
      struct.task_message = iprot.readString();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, task_code struct) throws org.apache.thrift.TException {
      oprot.writeString(struct.task_message);
    }

  }

  private static class task_codeTupleSchemeFactory implements SchemeFactory {
    public task_codeTupleScheme getScheme() {
      return new task_codeTupleScheme();
    }
  }

  private static class task_codeTupleScheme extends TupleScheme<task_code> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, task_code struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, task_code struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
    }
  }

}

