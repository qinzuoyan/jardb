/**
 * Autogenerated by Thrift Compiler (0.9.3)
 * <p>
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *
 * @generated
 */
package dsn.apps;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.server.AbstractNonblockingServer.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import javax.annotation.Generated;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked"})
@Generated(value = "Autogenerated by Thrift Compiler (0.9.3)", date = "2016-03-09")
public class meta {

    public interface Iface {

        public dsn.replication.query_cfg_response query_cfg(dsn.replication.query_cfg_request query) throws org.apache.thrift.TException;

    }

    public interface AsyncIface {

        public void query_cfg(dsn.replication.query_cfg_request query, org.apache.thrift.async.AsyncMethodCallback resultHandler) throws org.apache.thrift.TException;

    }

    public static class Client extends org.apache.thrift.TServiceClient implements Iface {
        public static class Factory implements org.apache.thrift.TServiceClientFactory<Client> {
            public Factory() {
            }

            public Client getClient(org.apache.thrift.protocol.TProtocol prot) {
                return new Client(prot);
            }

            public Client getClient(org.apache.thrift.protocol.TProtocol iprot, org.apache.thrift.protocol.TProtocol oprot) {
                return new Client(iprot, oprot);
            }
        }

        public Client(org.apache.thrift.protocol.TProtocol prot) {
            super(prot, prot);
        }

        public Client(org.apache.thrift.protocol.TProtocol iprot, org.apache.thrift.protocol.TProtocol oprot) {
            super(iprot, oprot);
        }

        public dsn.replication.query_cfg_response query_cfg(dsn.replication.query_cfg_request query) throws org.apache.thrift.TException {
            send_query_cfg(query);
            return recv_query_cfg();
        }

        public void send_query_cfg(dsn.replication.query_cfg_request query) throws org.apache.thrift.TException {
            query_cfg_args args = new query_cfg_args();
            args.setQuery(query);
            sendBase("query_cfg", args);
        }

        public dsn.replication.query_cfg_response recv_query_cfg() throws org.apache.thrift.TException {
            query_cfg_result result = new query_cfg_result();
            receiveBase(result, "query_cfg");
            if (result.isSetSuccess()) {
                return result.success;
            }
            throw new org.apache.thrift.TApplicationException(org.apache.thrift.TApplicationException.MISSING_RESULT, "query_cfg failed: unknown result");
        }

    }

    public static class AsyncClient extends org.apache.thrift.async.TAsyncClient implements AsyncIface {
        public static class Factory implements org.apache.thrift.async.TAsyncClientFactory<AsyncClient> {
            private org.apache.thrift.async.TAsyncClientManager clientManager;
            private org.apache.thrift.protocol.TProtocolFactory protocolFactory;

            public Factory(org.apache.thrift.async.TAsyncClientManager clientManager, org.apache.thrift.protocol.TProtocolFactory protocolFactory) {
                this.clientManager = clientManager;
                this.protocolFactory = protocolFactory;
            }

            public AsyncClient getAsyncClient(org.apache.thrift.transport.TNonblockingTransport transport) {
                return new AsyncClient(protocolFactory, clientManager, transport);
            }
        }

        public AsyncClient(org.apache.thrift.protocol.TProtocolFactory protocolFactory, org.apache.thrift.async.TAsyncClientManager clientManager, org.apache.thrift.transport.TNonblockingTransport transport) {
            super(protocolFactory, clientManager, transport);
        }

        public void query_cfg(dsn.replication.query_cfg_request query, org.apache.thrift.async.AsyncMethodCallback resultHandler) throws org.apache.thrift.TException {
            checkReady();
            query_cfg_call method_call = new query_cfg_call(query, resultHandler, this, ___protocolFactory, ___transport);
            this.___currentMethod = method_call;
            ___manager.call(method_call);
        }

        public static class query_cfg_call extends org.apache.thrift.async.TAsyncMethodCall {
            private dsn.replication.query_cfg_request query;

            public query_cfg_call(dsn.replication.query_cfg_request query, org.apache.thrift.async.AsyncMethodCallback resultHandler, org.apache.thrift.async.TAsyncClient client, org.apache.thrift.protocol.TProtocolFactory protocolFactory, org.apache.thrift.transport.TNonblockingTransport transport) throws org.apache.thrift.TException {
                super(client, protocolFactory, transport, resultHandler, false);
                this.query = query;
            }

            public void write_args(org.apache.thrift.protocol.TProtocol prot) throws org.apache.thrift.TException {
                prot.writeMessageBegin(new org.apache.thrift.protocol.TMessage("query_cfg", org.apache.thrift.protocol.TMessageType.CALL, 0));
                query_cfg_args args = new query_cfg_args();
                args.setQuery(query);
                args.write(prot);
                prot.writeMessageEnd();
            }

            public dsn.replication.query_cfg_response getResult() throws org.apache.thrift.TException {
                if (getState() != org.apache.thrift.async.TAsyncMethodCall.State.RESPONSE_READ) {
                    throw new IllegalStateException("Method call not finished!");
                }
                org.apache.thrift.transport.TMemoryInputTransport memoryTransport = new org.apache.thrift.transport.TMemoryInputTransport(getFrameBuffer().array());
                org.apache.thrift.protocol.TProtocol prot = client.getProtocolFactory().getProtocol(memoryTransport);
                return (new Client(prot)).recv_query_cfg();
            }
        }

    }

    public static class Processor<I extends Iface> extends org.apache.thrift.TBaseProcessor<I> implements org.apache.thrift.TProcessor {
        private static final Logger LOGGER = LoggerFactory.getLogger(Processor.class.getName());

        public Processor(I iface) {
            super(iface, getProcessMap(new HashMap<String, org.apache.thrift.ProcessFunction<I, ? extends org.apache.thrift.TBase>>()));
        }

        protected Processor(I iface, Map<String, org.apache.thrift.ProcessFunction<I, ? extends org.apache.thrift.TBase>> processMap) {
            super(iface, getProcessMap(processMap));
        }

        private static <I extends Iface> Map<String, org.apache.thrift.ProcessFunction<I, ? extends org.apache.thrift.TBase>> getProcessMap(Map<String, org.apache.thrift.ProcessFunction<I, ? extends org.apache.thrift.TBase>> processMap) {
            processMap.put("query_cfg", new query_cfg());
            return processMap;
        }

        public static class query_cfg<I extends Iface> extends org.apache.thrift.ProcessFunction<I, query_cfg_args> {
            public query_cfg() {
                super("query_cfg");
            }

            public query_cfg_args getEmptyArgsInstance() {
                return new query_cfg_args();
            }

            protected boolean isOneway() {
                return false;
            }

            public query_cfg_result getResult(I iface, query_cfg_args args) throws org.apache.thrift.TException {
                query_cfg_result result = new query_cfg_result();
                result.success = iface.query_cfg(args.query);
                return result;
            }
        }

    }

    public static class AsyncProcessor<I extends AsyncIface> extends org.apache.thrift.TBaseAsyncProcessor<I> {
        private static final Logger LOGGER = LoggerFactory.getLogger(AsyncProcessor.class.getName());

        public AsyncProcessor(I iface) {
            super(iface, getProcessMap(new HashMap<String, org.apache.thrift.AsyncProcessFunction<I, ? extends org.apache.thrift.TBase, ?>>()));
        }

        protected AsyncProcessor(I iface, Map<String, org.apache.thrift.AsyncProcessFunction<I, ? extends org.apache.thrift.TBase, ?>> processMap) {
            super(iface, getProcessMap(processMap));
        }

        private static <I extends AsyncIface> Map<String, org.apache.thrift.AsyncProcessFunction<I, ? extends org.apache.thrift.TBase, ?>> getProcessMap(Map<String, org.apache.thrift.AsyncProcessFunction<I, ? extends org.apache.thrift.TBase, ?>> processMap) {
            processMap.put("query_cfg", new query_cfg());
            return processMap;
        }

        public static class query_cfg<I extends AsyncIface> extends org.apache.thrift.AsyncProcessFunction<I, query_cfg_args, dsn.replication.query_cfg_response> {
            public query_cfg() {
                super("query_cfg");
            }

            public query_cfg_args getEmptyArgsInstance() {
                return new query_cfg_args();
            }

            public AsyncMethodCallback<dsn.replication.query_cfg_response> getResultHandler(final AsyncFrameBuffer fb, final int seqid) {
                final org.apache.thrift.AsyncProcessFunction fcall = this;
                return new AsyncMethodCallback<dsn.replication.query_cfg_response>() {
                    public void onComplete(dsn.replication.query_cfg_response o) {
                        query_cfg_result result = new query_cfg_result();
                        result.success = o;
                        try {
                            fcall.sendResponse(fb, result, org.apache.thrift.protocol.TMessageType.REPLY, seqid);
                            return;
                        } catch (Exception e) {
                            LOGGER.error("Exception writing to internal frame buffer", e);
                        }
                        fb.close();
                    }

                    public void onError(Exception e) {
                        byte msgType = org.apache.thrift.protocol.TMessageType.REPLY;
                        org.apache.thrift.TBase msg;
                        query_cfg_result result = new query_cfg_result();
                        {
                            msgType = org.apache.thrift.protocol.TMessageType.EXCEPTION;
                            msg = (org.apache.thrift.TBase) new org.apache.thrift.TApplicationException(org.apache.thrift.TApplicationException.INTERNAL_ERROR, e.getMessage());
                        }
                        try {
                            fcall.sendResponse(fb, msg, msgType, seqid);
                            return;
                        } catch (Exception ex) {
                            LOGGER.error("Exception writing to internal frame buffer", ex);
                        }
                        fb.close();
                    }
                };
            }

            protected boolean isOneway() {
                return false;
            }

            public void start(I iface, query_cfg_args args, org.apache.thrift.async.AsyncMethodCallback<dsn.replication.query_cfg_response> resultHandler) throws TException {
                iface.query_cfg(args.query, resultHandler);
            }
        }

    }

    public static class query_cfg_args implements org.apache.thrift.TBase<query_cfg_args, query_cfg_args._Fields>, java.io.Serializable, Cloneable, Comparable<query_cfg_args> {
        private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("query_cfg_args");

        private static final org.apache.thrift.protocol.TField QUERY_FIELD_DESC = new org.apache.thrift.protocol.TField("query", org.apache.thrift.protocol.TType.STRUCT, (short) 1);

        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();

        static {
            schemes.put(StandardScheme.class, new query_cfg_argsStandardSchemeFactory());
            schemes.put(TupleScheme.class, new query_cfg_argsTupleSchemeFactory());
        }

        public dsn.replication.query_cfg_request query; // required

        /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
        public enum _Fields implements org.apache.thrift.TFieldIdEnum {
            QUERY((short) 1, "query");

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
                switch (fieldId) {
                    case 1: // QUERY
                        return QUERY;
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
        public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;

        static {
            Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.QUERY, new org.apache.thrift.meta_data.FieldMetaData("query", org.apache.thrift.TFieldRequirementType.DEFAULT,
                    new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, dsn.replication.query_cfg_request.class)));
            metaDataMap = Collections.unmodifiableMap(tmpMap);
            org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(query_cfg_args.class, metaDataMap);
        }

        public query_cfg_args() {
        }

        public query_cfg_args(
                dsn.replication.query_cfg_request query) {
            this();
            this.query = query;
        }

        /**
         * Performs a deep copy on <i>other</i>.
         */
        public query_cfg_args(query_cfg_args other) {
            if (other.isSetQuery()) {
                this.query = new dsn.replication.query_cfg_request(other.query);
            }
        }

        public query_cfg_args deepCopy() {
            return new query_cfg_args(this);
        }

        @Override
        public void clear() {
            this.query = null;
        }

        public dsn.replication.query_cfg_request getQuery() {
            return this.query;
        }

        public query_cfg_args setQuery(dsn.replication.query_cfg_request query) {
            this.query = query;
            return this;
        }

        public void unsetQuery() {
            this.query = null;
        }

        /** Returns true if field query is set (has been assigned a value) and false otherwise */
        public boolean isSetQuery() {
            return this.query != null;
        }

        public void setQueryIsSet(boolean value) {
            if (!value) {
                this.query = null;
            }
        }

        public void setFieldValue(_Fields field, Object value) {
            switch (field) {
                case QUERY:
                    if (value == null) {
                        unsetQuery();
                    } else {
                        setQuery((dsn.replication.query_cfg_request) value);
                    }
                    break;

            }
        }

        public Object getFieldValue(_Fields field) {
            switch (field) {
                case QUERY:
                    return getQuery();

            }
            throw new IllegalStateException();
        }

        /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
        public boolean isSet(_Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }

            switch (field) {
                case QUERY:
                    return isSetQuery();
            }
            throw new IllegalStateException();
        }

        @Override
        public boolean equals(Object that) {
            if (that == null)
                return false;
            if (that instanceof query_cfg_args)
                return this.equals((query_cfg_args) that);
            return false;
        }

        public boolean equals(query_cfg_args that) {
            if (that == null)
                return false;

            boolean this_present_query = true && this.isSetQuery();
            boolean that_present_query = true && that.isSetQuery();
            if (this_present_query || that_present_query) {
                if (!(this_present_query && that_present_query))
                    return false;
                if (!this.query.equals(that.query))
                    return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            List<Object> list = new ArrayList<Object>();

            boolean present_query = true && (isSetQuery());
            list.add(present_query);
            if (present_query)
                list.add(query);

            return list.hashCode();
        }

        @Override
        public int compareTo(query_cfg_args other) {
            if (!getClass().equals(other.getClass())) {
                return getClass().getName().compareTo(other.getClass().getName());
            }

            int lastComparison = 0;

            lastComparison = Boolean.valueOf(isSetQuery()).compareTo(other.isSetQuery());
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (isSetQuery()) {
                lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.query, other.query);
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
            StringBuilder sb = new StringBuilder("query_cfg_args(");
            boolean first = true;

            sb.append("query:");
            if (this.query == null) {
                sb.append("null");
            } else {
                sb.append(this.query);
            }
            first = false;
            sb.append(")");
            return sb.toString();
        }

        public void validate() throws org.apache.thrift.TException {
            // check for required fields
            // check for sub-struct validity
            if (query != null) {
                query.validate();
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
                read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
            } catch (org.apache.thrift.TException te) {
                throw new java.io.IOException(te);
            }
        }

        private static class query_cfg_argsStandardSchemeFactory implements SchemeFactory {
            public query_cfg_argsStandardScheme getScheme() {
                return new query_cfg_argsStandardScheme();
            }
        }

        private static class query_cfg_argsStandardScheme extends StandardScheme<query_cfg_args> {

            public void read(org.apache.thrift.protocol.TProtocol iprot, query_cfg_args struct) throws org.apache.thrift.TException {
                org.apache.thrift.protocol.TField schemeField;
                iprot.readStructBegin();
                while (true) {
                    schemeField = iprot.readFieldBegin();
                    if (schemeField.type == org.apache.thrift.protocol.TType.STOP) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 1: // QUERY
                            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
                                struct.query = new dsn.replication.query_cfg_request();
                                struct.query.read(iprot);
                                struct.setQueryIsSet(true);
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

            public void write(org.apache.thrift.protocol.TProtocol oprot, query_cfg_args struct) throws org.apache.thrift.TException {
                struct.validate();

                oprot.writeStructBegin(STRUCT_DESC);
                if (struct.query != null) {
                    oprot.writeFieldBegin(QUERY_FIELD_DESC);
                    struct.query.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }

        }

        private static class query_cfg_argsTupleSchemeFactory implements SchemeFactory {
            public query_cfg_argsTupleScheme getScheme() {
                return new query_cfg_argsTupleScheme();
            }
        }

        private static class query_cfg_argsTupleScheme extends TupleScheme<query_cfg_args> {

            @Override
            public void write(org.apache.thrift.protocol.TProtocol prot, query_cfg_args struct) throws org.apache.thrift.TException {
                TTupleProtocol oprot = (TTupleProtocol) prot;
                BitSet optionals = new BitSet();
                if (struct.isSetQuery()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetQuery()) {
                    struct.query.write(oprot);
                }
            }

            @Override
            public void read(org.apache.thrift.protocol.TProtocol prot, query_cfg_args struct) throws org.apache.thrift.TException {
                TTupleProtocol iprot = (TTupleProtocol) prot;
                BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.query = new dsn.replication.query_cfg_request();
                    struct.query.read(iprot);
                    struct.setQueryIsSet(true);
                }
            }
        }

    }

    public static class query_cfg_result implements org.apache.thrift.TBase<query_cfg_result, query_cfg_result._Fields>, java.io.Serializable, Cloneable, Comparable<query_cfg_result> {
        private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("query_cfg_result");

        private static final org.apache.thrift.protocol.TField SUCCESS_FIELD_DESC = new org.apache.thrift.protocol.TField("success", org.apache.thrift.protocol.TType.STRUCT, (short) 0);

        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();

        static {
            schemes.put(StandardScheme.class, new query_cfg_resultStandardSchemeFactory());
            schemes.put(TupleScheme.class, new query_cfg_resultTupleSchemeFactory());
        }

        public dsn.replication.query_cfg_response success; // required

        /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
        public enum _Fields implements org.apache.thrift.TFieldIdEnum {
            SUCCESS((short) 0, "success");

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
                switch (fieldId) {
                    case 0: // SUCCESS
                        return SUCCESS;
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
        public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;

        static {
            Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.SUCCESS, new org.apache.thrift.meta_data.FieldMetaData("success", org.apache.thrift.TFieldRequirementType.DEFAULT,
                    new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, dsn.replication.query_cfg_response.class)));
            metaDataMap = Collections.unmodifiableMap(tmpMap);
            org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(query_cfg_result.class, metaDataMap);
        }

        public query_cfg_result() {
        }

        public query_cfg_result(
                dsn.replication.query_cfg_response success) {
            this();
            this.success = success;
        }

        /**
         * Performs a deep copy on <i>other</i>.
         */
        public query_cfg_result(query_cfg_result other) {
            if (other.isSetSuccess()) {
                this.success = new dsn.replication.query_cfg_response(other.success);
            }
        }

        public query_cfg_result deepCopy() {
            return new query_cfg_result(this);
        }

        @Override
        public void clear() {
            this.success = null;
        }

        public dsn.replication.query_cfg_response getSuccess() {
            return this.success;
        }

        public query_cfg_result setSuccess(dsn.replication.query_cfg_response success) {
            this.success = success;
            return this;
        }

        public void unsetSuccess() {
            this.success = null;
        }

        /** Returns true if field success is set (has been assigned a value) and false otherwise */
        public boolean isSetSuccess() {
            return this.success != null;
        }

        public void setSuccessIsSet(boolean value) {
            if (!value) {
                this.success = null;
            }
        }

        public void setFieldValue(_Fields field, Object value) {
            switch (field) {
                case SUCCESS:
                    if (value == null) {
                        unsetSuccess();
                    } else {
                        setSuccess((dsn.replication.query_cfg_response) value);
                    }
                    break;

            }
        }

        public Object getFieldValue(_Fields field) {
            switch (field) {
                case SUCCESS:
                    return getSuccess();

            }
            throw new IllegalStateException();
        }

        /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
        public boolean isSet(_Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }

            switch (field) {
                case SUCCESS:
                    return isSetSuccess();
            }
            throw new IllegalStateException();
        }

        @Override
        public boolean equals(Object that) {
            if (that == null)
                return false;
            if (that instanceof query_cfg_result)
                return this.equals((query_cfg_result) that);
            return false;
        }

        public boolean equals(query_cfg_result that) {
            if (that == null)
                return false;

            boolean this_present_success = true && this.isSetSuccess();
            boolean that_present_success = true && that.isSetSuccess();
            if (this_present_success || that_present_success) {
                if (!(this_present_success && that_present_success))
                    return false;
                if (!this.success.equals(that.success))
                    return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            List<Object> list = new ArrayList<Object>();

            boolean present_success = true && (isSetSuccess());
            list.add(present_success);
            if (present_success)
                list.add(success);

            return list.hashCode();
        }

        @Override
        public int compareTo(query_cfg_result other) {
            if (!getClass().equals(other.getClass())) {
                return getClass().getName().compareTo(other.getClass().getName());
            }

            int lastComparison = 0;

            lastComparison = Boolean.valueOf(isSetSuccess()).compareTo(other.isSetSuccess());
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (isSetSuccess()) {
                lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.success, other.success);
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
            StringBuilder sb = new StringBuilder("query_cfg_result(");
            boolean first = true;

            sb.append("success:");
            if (this.success == null) {
                sb.append("null");
            } else {
                sb.append(this.success);
            }
            first = false;
            sb.append(")");
            return sb.toString();
        }

        public void validate() throws org.apache.thrift.TException {
            // check for required fields
            // check for sub-struct validity
            if (success != null) {
                success.validate();
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
                read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
            } catch (org.apache.thrift.TException te) {
                throw new java.io.IOException(te);
            }
        }

        private static class query_cfg_resultStandardSchemeFactory implements SchemeFactory {
            public query_cfg_resultStandardScheme getScheme() {
                return new query_cfg_resultStandardScheme();
            }
        }

        private static class query_cfg_resultStandardScheme extends StandardScheme<query_cfg_result> {

            public void read(org.apache.thrift.protocol.TProtocol iprot, query_cfg_result struct) throws org.apache.thrift.TException {
                org.apache.thrift.protocol.TField schemeField;
                iprot.readStructBegin();
                while (true) {
                    schemeField = iprot.readFieldBegin();
                    if (schemeField.type == org.apache.thrift.protocol.TType.STOP) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 0: // SUCCESS
                            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
                                struct.success = new dsn.replication.query_cfg_response();
                                struct.success.read(iprot);
                                struct.setSuccessIsSet(true);
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

            public void write(org.apache.thrift.protocol.TProtocol oprot, query_cfg_result struct) throws org.apache.thrift.TException {
                struct.validate();

                oprot.writeStructBegin(STRUCT_DESC);
                if (struct.success != null) {
                    oprot.writeFieldBegin(SUCCESS_FIELD_DESC);
                    struct.success.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }

        }

        private static class query_cfg_resultTupleSchemeFactory implements SchemeFactory {
            public query_cfg_resultTupleScheme getScheme() {
                return new query_cfg_resultTupleScheme();
            }
        }

        private static class query_cfg_resultTupleScheme extends TupleScheme<query_cfg_result> {

            @Override
            public void write(org.apache.thrift.protocol.TProtocol prot, query_cfg_result struct) throws org.apache.thrift.TException {
                TTupleProtocol oprot = (TTupleProtocol) prot;
                BitSet optionals = new BitSet();
                if (struct.isSetSuccess()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetSuccess()) {
                    struct.success.write(oprot);
                }
            }

            @Override
            public void read(org.apache.thrift.protocol.TProtocol prot, query_cfg_result struct) throws org.apache.thrift.TException {
                TTupleProtocol iprot = (TTupleProtocol) prot;
                BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.success = new dsn.replication.query_cfg_response();
                    struct.success.read(iprot);
                    struct.setSuccessIsSet(true);
                }
            }
        }

    }

}
