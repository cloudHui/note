package net.proto;

import com.google.protobuf.AbstractParser;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.GeneratedMessage.FieldAccessorTable;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.Parser;
import com.google.protobuf.UnknownFieldSet;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamException;

public final class CommonProto {
	private static Descriptor internal_static_proto_KVPair_descriptor;
	private static FieldAccessorTable internal_static_proto_KVPair_fieldAccessorTable;
	private static Descriptor internal_static_proto_KStrVPair_descriptor;
	private static FieldAccessorTable internal_static_proto_KStrVPair_fieldAccessorTable;
	private static Descriptor internal_static_proto_Heartbeat_descriptor;
	private static FieldAccessorTable internal_static_proto_Heartbeat_fieldAccessorTable;
	private static Descriptor internal_static_proto_ReqRegister_descriptor;
	private static FieldAccessorTable internal_static_proto_ReqRegister_fieldAccessorTable;
	private static Descriptor internal_static_proto_AckRegister_descriptor;
	private static FieldAccessorTable internal_static_proto_AckRegister_fieldAccessorTable;
	private static FileDescriptor descriptor;

	private CommonProto() {
	}

	public static void registerAllExtensions(ExtensionRegistry registry) {
	}

	public static FileDescriptor getDescriptor() {
		return descriptor;
	}

	static {
		String[] descriptorData = new String[]{"\n\fcommon.proto\u0012\u0005proto\"$\n\u0006KVPair\u0012\u000b\n\u0003key\u0018\u0001 \u0001(\u0003\u0012\r\n\u0005value\u0018\u0002 \u0001(\u0003\"'\n\tKStrVPair\u0012\u000b\n\u0003key\u0018\u0001 \u0001(\u0003\u0012\r\n\u0005value\u0018\u0002 \u0001(\f\"\u001a\n\tHeartbeat\u0012\r\n\u0005times\u0018\u0001 \u0001(\u0003\"Y\n\u000bReqRegister\u0012\u0010\n\bserverId\u0018\u0001 \u0002(\u0003\u0012\u0012\n\nserverType\u0018\u0002 \u0002(\u0005\u0012\u0010\n\bserverIP\u0018\u0003 \u0001(\f\u0012\u0012\n\nserverPort\u0018\u0004 \u0001(\u0005\"U\n\u000bAckRegister\u0012\u000e\n\u0006result\u0018\u0001 \u0002(\u0005\u0012\u0010\n\bserverId\u0018\u0002 \u0002(\u0003\u0012\u0010\n\bserverIP\u0018\u0003 \u0001(\f\u0012\u0012\n\nserverPort\u0018\u0004 \u0001(\u0005B\rB\u000bCommonProto"};
		InternalDescriptorAssigner assigner = new InternalDescriptorAssigner() {
			public ExtensionRegistry assignDescriptors(FileDescriptor root) {
				CommonProto.descriptor = root;
				CommonProto.internal_static_proto_KVPair_descriptor = (Descriptor) CommonProto.getDescriptor().getMessageTypes().get(0);
				CommonProto.internal_static_proto_KVPair_fieldAccessorTable = new FieldAccessorTable(CommonProto.internal_static_proto_KVPair_descriptor, new String[]{"Key", "Value"});
				CommonProto.internal_static_proto_KStrVPair_descriptor = (Descriptor) CommonProto.getDescriptor().getMessageTypes().get(1);
				CommonProto.internal_static_proto_KStrVPair_fieldAccessorTable = new FieldAccessorTable(CommonProto.internal_static_proto_KStrVPair_descriptor, new String[]{"Key", "Value"});
				CommonProto.internal_static_proto_Heartbeat_descriptor = (Descriptor) CommonProto.getDescriptor().getMessageTypes().get(2);
				CommonProto.internal_static_proto_Heartbeat_fieldAccessorTable = new FieldAccessorTable(CommonProto.internal_static_proto_Heartbeat_descriptor, new String[]{"Times"});
				CommonProto.internal_static_proto_ReqRegister_descriptor = (Descriptor) CommonProto.getDescriptor().getMessageTypes().get(3);
				CommonProto.internal_static_proto_ReqRegister_fieldAccessorTable = new FieldAccessorTable(CommonProto.internal_static_proto_ReqRegister_descriptor, new String[]{"ServerId", "ServerType", "ServerIP", "ServerPort"});
				CommonProto.internal_static_proto_AckRegister_descriptor = (Descriptor) CommonProto.getDescriptor().getMessageTypes().get(4);
				CommonProto.internal_static_proto_AckRegister_fieldAccessorTable = new FieldAccessorTable(CommonProto.internal_static_proto_AckRegister_descriptor, new String[]{"Result", "ServerId", "ServerIP", "ServerPort"});
				return null;
			}
		};
		FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new FileDescriptor[0], assigner);
	}

	public static final class AckRegister extends GeneratedMessage implements CommonProto.AckRegisterOrBuilder {
		private static final CommonProto.AckRegister defaultInstance = new CommonProto.AckRegister(true);
		private final UnknownFieldSet unknownFields;
		public static Parser<CommonProto.AckRegister> PARSER = new AbstractParser<CommonProto.AckRegister>() {
			public CommonProto.AckRegister parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
				return new CommonProto.AckRegister(input, extensionRegistry);
			}
		};
		private int bitField0_;
		public static final int RESULT_FIELD_NUMBER = 1;
		private int result_;
		public static final int SERVERID_FIELD_NUMBER = 2;
		private long serverId_;
		public static final int SERVERIP_FIELD_NUMBER = 3;
		private ByteString serverIP_;
		public static final int SERVERPORT_FIELD_NUMBER = 4;
		private int serverPort_;
		private byte memoizedIsInitialized;
		private int memoizedSerializedSize;
		private static final long serialVersionUID = 0L;

		private AckRegister(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
			super(builder);
			this.memoizedIsInitialized = -1;
			this.memoizedSerializedSize = -1;
			this.unknownFields = builder.getUnknownFields();
		}

		private AckRegister(boolean noInit) {
			this.memoizedIsInitialized = -1;
			this.memoizedSerializedSize = -1;
			this.unknownFields = UnknownFieldSet.getDefaultInstance();
		}

		public static CommonProto.AckRegister getDefaultInstance() {
			return defaultInstance;
		}

		public CommonProto.AckRegister getDefaultInstanceForType() {
			return defaultInstance;
		}

		public final UnknownFieldSet getUnknownFields() {
			return this.unknownFields;
		}

		private AckRegister(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
			this.memoizedIsInitialized = -1;
			this.memoizedSerializedSize = -1;
			this.initFields();
			com.google.protobuf.UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

			try {
				boolean done = false;

				while (!done) {
					int tag = input.readTag();
					switch (tag) {
						case 0:
							done = true;
							break;
						case 8:
							this.bitField0_ |= 1;
							this.result_ = input.readInt32();
							break;
						case 16:
							this.bitField0_ |= 2;
							this.serverId_ = input.readInt64();
							break;
						case 26:
							this.bitField0_ |= 4;
							this.serverIP_ = input.readBytes();
							break;
						case 32:
							this.bitField0_ |= 8;
							this.serverPort_ = input.readInt32();
							break;
						default:
							if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
								done = true;
							}
					}
				}
			} catch (InvalidProtocolBufferException var11) {
				throw var11.setUnfinishedMessage(this);
			} catch (IOException var12) {
				throw (new InvalidProtocolBufferException(var12.getMessage())).setUnfinishedMessage(this);
			} finally {
				this.unknownFields = unknownFields.build();
				this.makeExtensionsImmutable();
			}

		}

		public static final Descriptor getDescriptor() {
			return CommonProto.internal_static_proto_AckRegister_descriptor;
		}

		protected FieldAccessorTable internalGetFieldAccessorTable() {
			return CommonProto.internal_static_proto_AckRegister_fieldAccessorTable.ensureFieldAccessorsInitialized(CommonProto.AckRegister.class, CommonProto.AckRegister.Builder.class);
		}

		public Parser<CommonProto.AckRegister> getParserForType() {
			return PARSER;
		}

		public boolean hasResult() {
			return (this.bitField0_ & 1) == 1;
		}

		public int getResult() {
			return this.result_;
		}

		public boolean hasServerId() {
			return (this.bitField0_ & 2) == 2;
		}

		public long getServerId() {
			return this.serverId_;
		}

		public boolean hasServerIP() {
			return (this.bitField0_ & 4) == 4;
		}

		public ByteString getServerIP() {
			return this.serverIP_;
		}

		public boolean hasServerPort() {
			return (this.bitField0_ & 8) == 8;
		}

		public int getServerPort() {
			return this.serverPort_;
		}

		private void initFields() {
			this.result_ = 0;
			this.serverId_ = 0L;
			this.serverIP_ = ByteString.EMPTY;
			this.serverPort_ = 0;
		}

		public final boolean isInitialized() {
			byte isInitialized = this.memoizedIsInitialized;
			if (isInitialized != -1) {
				return isInitialized == 1;
			} else if (!this.hasResult()) {
				this.memoizedIsInitialized = 0;
				return false;
			} else if (!this.hasServerId()) {
				this.memoizedIsInitialized = 0;
				return false;
			} else {
				this.memoizedIsInitialized = 1;
				return true;
			}
		}

		public void writeTo(CodedOutputStream output) throws IOException {
			this.getSerializedSize();
			if ((this.bitField0_ & 1) == 1) {
				output.writeInt32(1, this.result_);
			}

			if ((this.bitField0_ & 2) == 2) {
				output.writeInt64(2, this.serverId_);
			}

			if ((this.bitField0_ & 4) == 4) {
				output.writeBytes(3, this.serverIP_);
			}

			if ((this.bitField0_ & 8) == 8) {
				output.writeInt32(4, this.serverPort_);
			}

			this.getUnknownFields().writeTo(output);
		}

		public int getSerializedSize() {
			int size = this.memoizedSerializedSize;
			if (size != -1) {
				return size;
			} else {
				size = 0;
				if ((this.bitField0_ & 1) == 1) {
					size += CodedOutputStream.computeInt32Size(1, this.result_);
				}

				if ((this.bitField0_ & 2) == 2) {
					size += CodedOutputStream.computeInt64Size(2, this.serverId_);
				}

				if ((this.bitField0_ & 4) == 4) {
					size += CodedOutputStream.computeBytesSize(3, this.serverIP_);
				}

				if ((this.bitField0_ & 8) == 8) {
					size += CodedOutputStream.computeInt32Size(4, this.serverPort_);
				}

				size += this.getUnknownFields().getSerializedSize();
				this.memoizedSerializedSize = size;
				return size;
			}
		}

		protected Object writeReplace() throws ObjectStreamException {
			return super.writeReplace();
		}

		public static CommonProto.AckRegister parseFrom(ByteString data) throws InvalidProtocolBufferException {
			return (CommonProto.AckRegister) PARSER.parseFrom(data);
		}

		public static CommonProto.AckRegister parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
			return (CommonProto.AckRegister) PARSER.parseFrom(data, extensionRegistry);
		}

		public static CommonProto.AckRegister parseFrom(byte[] data) throws InvalidProtocolBufferException {
			return (CommonProto.AckRegister) PARSER.parseFrom(data);
		}

		public static CommonProto.AckRegister parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
			return (CommonProto.AckRegister) PARSER.parseFrom(data, extensionRegistry);
		}

		public static CommonProto.AckRegister parseFrom(InputStream input) throws IOException {
			return (CommonProto.AckRegister) PARSER.parseFrom(input);
		}

		public static CommonProto.AckRegister parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
			return (CommonProto.AckRegister) PARSER.parseFrom(input, extensionRegistry);
		}

		public static CommonProto.AckRegister parseDelimitedFrom(InputStream input) throws IOException {
			return (CommonProto.AckRegister) PARSER.parseDelimitedFrom(input);
		}

		public static CommonProto.AckRegister parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
			return (CommonProto.AckRegister) PARSER.parseDelimitedFrom(input, extensionRegistry);
		}

		public static CommonProto.AckRegister parseFrom(CodedInputStream input) throws IOException {
			return (CommonProto.AckRegister) PARSER.parseFrom(input);
		}

		public static CommonProto.AckRegister parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
			return (CommonProto.AckRegister) PARSER.parseFrom(input, extensionRegistry);
		}

		public static CommonProto.AckRegister.Builder newBuilder() {
			return CommonProto.AckRegister.Builder.create();
		}

		public CommonProto.AckRegister.Builder newBuilderForType() {
			return newBuilder();
		}

		public static CommonProto.AckRegister.Builder newBuilder(CommonProto.AckRegister prototype) {
			return newBuilder().mergeFrom(prototype);
		}

		public CommonProto.AckRegister.Builder toBuilder() {
			return newBuilder(this);
		}

		protected CommonProto.AckRegister.Builder newBuilderForType(BuilderParent parent) {
			CommonProto.AckRegister.Builder builder = new CommonProto.AckRegister.Builder(parent);
			return builder;
		}

		static {
			defaultInstance.initFields();
		}

		public static final class Builder extends com.google.protobuf.GeneratedMessage.Builder<CommonProto.AckRegister.Builder> implements CommonProto.AckRegisterOrBuilder {
			private int bitField0_;
			private int result_;
			private long serverId_;
			private ByteString serverIP_;
			private int serverPort_;

			public static final Descriptor getDescriptor() {
				return CommonProto.internal_static_proto_AckRegister_descriptor;
			}

			protected FieldAccessorTable internalGetFieldAccessorTable() {
				return CommonProto.internal_static_proto_AckRegister_fieldAccessorTable.ensureFieldAccessorsInitialized(CommonProto.AckRegister.class, CommonProto.AckRegister.Builder.class);
			}

			private Builder() {
				this.serverIP_ = ByteString.EMPTY;
				this.maybeForceBuilderInitialization();
			}

			private Builder(BuilderParent parent) {
				super(parent);
				this.serverIP_ = ByteString.EMPTY;
				this.maybeForceBuilderInitialization();
			}

			private void maybeForceBuilderInitialization() {
				if (CommonProto.AckRegister.alwaysUseFieldBuilders) {
				}

			}

			private static CommonProto.AckRegister.Builder create() {
				return new CommonProto.AckRegister.Builder();
			}

			public CommonProto.AckRegister.Builder clear() {
				super.clear();
				this.result_ = 0;
				this.bitField0_ &= -2;
				this.serverId_ = 0L;
				this.bitField0_ &= -3;
				this.serverIP_ = ByteString.EMPTY;
				this.bitField0_ &= -5;
				this.serverPort_ = 0;
				this.bitField0_ &= -9;
				return this;
			}

			public CommonProto.AckRegister.Builder clone() {
				return create().mergeFrom(this.buildPartial());
			}

			public Descriptor getDescriptorForType() {
				return CommonProto.internal_static_proto_AckRegister_descriptor;
			}

			public CommonProto.AckRegister getDefaultInstanceForType() {
				return CommonProto.AckRegister.getDefaultInstance();
			}

			public CommonProto.AckRegister build() {
				CommonProto.AckRegister result = this.buildPartial();
				if (!result.isInitialized()) {
					throw newUninitializedMessageException(result);
				} else {
					return result;
				}
			}

			public CommonProto.AckRegister buildPartial() {
				CommonProto.AckRegister result = new CommonProto.AckRegister(this);
				int from_bitField0_ = this.bitField0_;
				int to_bitField0_ = 0;
				if ((from_bitField0_ & 1) == 1) {
					to_bitField0_ |= 1;
				}

				result.result_ = this.result_;
				if ((from_bitField0_ & 2) == 2) {
					to_bitField0_ |= 2;
				}

				result.serverId_ = this.serverId_;
				if ((from_bitField0_ & 4) == 4) {
					to_bitField0_ |= 4;
				}

				result.serverIP_ = this.serverIP_;
				if ((from_bitField0_ & 8) == 8) {
					to_bitField0_ |= 8;
				}

				result.serverPort_ = this.serverPort_;
				result.bitField0_ = to_bitField0_;
				this.onBuilt();
				return result;
			}

			public CommonProto.AckRegister.Builder mergeFrom(Message other) {
				if (other instanceof CommonProto.AckRegister) {
					return this.mergeFrom((CommonProto.AckRegister) other);
				} else {
					super.mergeFrom(other);
					return this;
				}
			}

			public CommonProto.AckRegister.Builder mergeFrom(CommonProto.AckRegister other) {
				if (other == CommonProto.AckRegister.getDefaultInstance()) {
					return this;
				} else {
					if (other.hasResult()) {
						this.setResult(other.getResult());
					}

					if (other.hasServerId()) {
						this.setServerId(other.getServerId());
					}

					if (other.hasServerIP()) {
						this.setServerIP(other.getServerIP());
					}

					if (other.hasServerPort()) {
						this.setServerPort(other.getServerPort());
					}

					this.mergeUnknownFields(other.getUnknownFields());
					return this;
				}
			}

			public final boolean isInitialized() {
				if (!this.hasResult()) {
					return false;
				} else {
					return this.hasServerId();
				}
			}

			public CommonProto.AckRegister.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
				CommonProto.AckRegister parsedMessage = null;

				try {
					parsedMessage = (CommonProto.AckRegister) CommonProto.AckRegister.PARSER.parsePartialFrom(input, extensionRegistry);
				} catch (InvalidProtocolBufferException var8) {
					parsedMessage = (CommonProto.AckRegister) var8.getUnfinishedMessage();
					throw var8;
				} finally {
					if (parsedMessage != null) {
						this.mergeFrom(parsedMessage);
					}

				}

				return this;
			}

			public boolean hasResult() {
				return (this.bitField0_ & 1) == 1;
			}

			public int getResult() {
				return this.result_;
			}

			public CommonProto.AckRegister.Builder setResult(int value) {
				this.bitField0_ |= 1;
				this.result_ = value;
				this.onChanged();
				return this;
			}

			public CommonProto.AckRegister.Builder clearResult() {
				this.bitField0_ &= -2;
				this.result_ = 0;
				this.onChanged();
				return this;
			}

			public boolean hasServerId() {
				return (this.bitField0_ & 2) == 2;
			}

			public long getServerId() {
				return this.serverId_;
			}

			public CommonProto.AckRegister.Builder setServerId(long value) {
				this.bitField0_ |= 2;
				this.serverId_ = value;
				this.onChanged();
				return this;
			}

			public CommonProto.AckRegister.Builder clearServerId() {
				this.bitField0_ &= -3;
				this.serverId_ = 0L;
				this.onChanged();
				return this;
			}

			public boolean hasServerIP() {
				return (this.bitField0_ & 4) == 4;
			}

			public ByteString getServerIP() {
				return this.serverIP_;
			}

			public CommonProto.AckRegister.Builder setServerIP(ByteString value) {
				if (value == null) {
					throw new NullPointerException();
				} else {
					this.bitField0_ |= 4;
					this.serverIP_ = value;
					this.onChanged();
					return this;
				}
			}

			public CommonProto.AckRegister.Builder clearServerIP() {
				this.bitField0_ &= -5;
				this.serverIP_ = CommonProto.AckRegister.getDefaultInstance().getServerIP();
				this.onChanged();
				return this;
			}

			public boolean hasServerPort() {
				return (this.bitField0_ & 8) == 8;
			}

			public int getServerPort() {
				return this.serverPort_;
			}

			public CommonProto.AckRegister.Builder setServerPort(int value) {
				this.bitField0_ |= 8;
				this.serverPort_ = value;
				this.onChanged();
				return this;
			}

			public CommonProto.AckRegister.Builder clearServerPort() {
				this.bitField0_ &= -9;
				this.serverPort_ = 0;
				this.onChanged();
				return this;
			}
		}
	}

	public interface AckRegisterOrBuilder extends MessageOrBuilder {
		boolean hasResult();

		int getResult();

		boolean hasServerId();

		long getServerId();

		boolean hasServerIP();

		ByteString getServerIP();

		boolean hasServerPort();

		int getServerPort();
	}

	public static final class ReqRegister extends GeneratedMessage implements CommonProto.ReqRegisterOrBuilder {
		private static final CommonProto.ReqRegister defaultInstance = new CommonProto.ReqRegister(true);
		private final UnknownFieldSet unknownFields;
		public static Parser<CommonProto.ReqRegister> PARSER = new AbstractParser<CommonProto.ReqRegister>() {
			public CommonProto.ReqRegister parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
				return new CommonProto.ReqRegister(input, extensionRegistry);
			}
		};
		private int bitField0_;
		public static final int SERVERID_FIELD_NUMBER = 1;
		private long serverId_;
		public static final int SERVERTYPE_FIELD_NUMBER = 2;
		private int serverType_;
		public static final int SERVERIP_FIELD_NUMBER = 3;
		private ByteString serverIP_;
		public static final int SERVERPORT_FIELD_NUMBER = 4;
		private int serverPort_;
		private byte memoizedIsInitialized;
		private int memoizedSerializedSize;
		private static final long serialVersionUID = 0L;

		private ReqRegister(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
			super(builder);
			this.memoizedIsInitialized = -1;
			this.memoizedSerializedSize = -1;
			this.unknownFields = builder.getUnknownFields();
		}

		private ReqRegister(boolean noInit) {
			this.memoizedIsInitialized = -1;
			this.memoizedSerializedSize = -1;
			this.unknownFields = UnknownFieldSet.getDefaultInstance();
		}

		public static CommonProto.ReqRegister getDefaultInstance() {
			return defaultInstance;
		}

		public CommonProto.ReqRegister getDefaultInstanceForType() {
			return defaultInstance;
		}

		public final UnknownFieldSet getUnknownFields() {
			return this.unknownFields;
		}

		private ReqRegister(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
			this.memoizedIsInitialized = -1;
			this.memoizedSerializedSize = -1;
			this.initFields();
			com.google.protobuf.UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

			try {
				boolean done = false;

				while (!done) {
					int tag = input.readTag();
					switch (tag) {
						case 0:
							done = true;
							break;
						case 8:
							this.bitField0_ |= 1;
							this.serverId_ = input.readInt64();
							break;
						case 16:
							this.bitField0_ |= 2;
							this.serverType_ = input.readInt32();
							break;
						case 26:
							this.bitField0_ |= 4;
							this.serverIP_ = input.readBytes();
							break;
						case 32:
							this.bitField0_ |= 8;
							this.serverPort_ = input.readInt32();
							break;
						default:
							if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
								done = true;
							}
					}
				}
			} catch (InvalidProtocolBufferException var11) {
				throw var11.setUnfinishedMessage(this);
			} catch (IOException var12) {
				throw (new InvalidProtocolBufferException(var12.getMessage())).setUnfinishedMessage(this);
			} finally {
				this.unknownFields = unknownFields.build();
				this.makeExtensionsImmutable();
			}

		}

		public static final Descriptor getDescriptor() {
			return CommonProto.internal_static_proto_ReqRegister_descriptor;
		}

		protected FieldAccessorTable internalGetFieldAccessorTable() {
			return CommonProto.internal_static_proto_ReqRegister_fieldAccessorTable.ensureFieldAccessorsInitialized(CommonProto.ReqRegister.class, CommonProto.ReqRegister.Builder.class);
		}

		public Parser<CommonProto.ReqRegister> getParserForType() {
			return PARSER;
		}

		public boolean hasServerId() {
			return (this.bitField0_ & 1) == 1;
		}

		public long getServerId() {
			return this.serverId_;
		}

		public boolean hasServerType() {
			return (this.bitField0_ & 2) == 2;
		}

		public int getServerType() {
			return this.serverType_;
		}

		public boolean hasServerIP() {
			return (this.bitField0_ & 4) == 4;
		}

		public ByteString getServerIP() {
			return this.serverIP_;
		}

		public boolean hasServerPort() {
			return (this.bitField0_ & 8) == 8;
		}

		public int getServerPort() {
			return this.serverPort_;
		}

		private void initFields() {
			this.serverId_ = 0L;
			this.serverType_ = 0;
			this.serverIP_ = ByteString.EMPTY;
			this.serverPort_ = 0;
		}

		public final boolean isInitialized() {
			byte isInitialized = this.memoizedIsInitialized;
			if (isInitialized != -1) {
				return isInitialized == 1;
			} else if (!this.hasServerId()) {
				this.memoizedIsInitialized = 0;
				return false;
			} else if (!this.hasServerType()) {
				this.memoizedIsInitialized = 0;
				return false;
			} else {
				this.memoizedIsInitialized = 1;
				return true;
			}
		}

		public void writeTo(CodedOutputStream output) throws IOException {
			this.getSerializedSize();
			if ((this.bitField0_ & 1) == 1) {
				output.writeInt64(1, this.serverId_);
			}

			if ((this.bitField0_ & 2) == 2) {
				output.writeInt32(2, this.serverType_);
			}

			if ((this.bitField0_ & 4) == 4) {
				output.writeBytes(3, this.serverIP_);
			}

			if ((this.bitField0_ & 8) == 8) {
				output.writeInt32(4, this.serverPort_);
			}

			this.getUnknownFields().writeTo(output);
		}

		public int getSerializedSize() {
			int size = this.memoizedSerializedSize;
			if (size != -1) {
				return size;
			} else {
				size = 0;
				if ((this.bitField0_ & 1) == 1) {
					size += CodedOutputStream.computeInt64Size(1, this.serverId_);
				}

				if ((this.bitField0_ & 2) == 2) {
					size += CodedOutputStream.computeInt32Size(2, this.serverType_);
				}

				if ((this.bitField0_ & 4) == 4) {
					size += CodedOutputStream.computeBytesSize(3, this.serverIP_);
				}

				if ((this.bitField0_ & 8) == 8) {
					size += CodedOutputStream.computeInt32Size(4, this.serverPort_);
				}

				size += this.getUnknownFields().getSerializedSize();
				this.memoizedSerializedSize = size;
				return size;
			}
		}

		protected Object writeReplace() throws ObjectStreamException {
			return super.writeReplace();
		}

		public static CommonProto.ReqRegister parseFrom(ByteString data) throws InvalidProtocolBufferException {
			return (CommonProto.ReqRegister) PARSER.parseFrom(data);
		}

		public static CommonProto.ReqRegister parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
			return (CommonProto.ReqRegister) PARSER.parseFrom(data, extensionRegistry);
		}

		public static CommonProto.ReqRegister parseFrom(byte[] data) throws InvalidProtocolBufferException {
			return (CommonProto.ReqRegister) PARSER.parseFrom(data);
		}

		public static CommonProto.ReqRegister parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
			return (CommonProto.ReqRegister) PARSER.parseFrom(data, extensionRegistry);
		}

		public static CommonProto.ReqRegister parseFrom(InputStream input) throws IOException {
			return (CommonProto.ReqRegister) PARSER.parseFrom(input);
		}

		public static CommonProto.ReqRegister parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
			return (CommonProto.ReqRegister) PARSER.parseFrom(input, extensionRegistry);
		}

		public static CommonProto.ReqRegister parseDelimitedFrom(InputStream input) throws IOException {
			return (CommonProto.ReqRegister) PARSER.parseDelimitedFrom(input);
		}

		public static CommonProto.ReqRegister parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
			return (CommonProto.ReqRegister) PARSER.parseDelimitedFrom(input, extensionRegistry);
		}

		public static CommonProto.ReqRegister parseFrom(CodedInputStream input) throws IOException {
			return (CommonProto.ReqRegister) PARSER.parseFrom(input);
		}

		public static CommonProto.ReqRegister parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
			return (CommonProto.ReqRegister) PARSER.parseFrom(input, extensionRegistry);
		}

		public static CommonProto.ReqRegister.Builder newBuilder() {
			return CommonProto.ReqRegister.Builder.create();
		}

		public CommonProto.ReqRegister.Builder newBuilderForType() {
			return newBuilder();
		}

		public static CommonProto.ReqRegister.Builder newBuilder(CommonProto.ReqRegister prototype) {
			return newBuilder().mergeFrom(prototype);
		}

		public CommonProto.ReqRegister.Builder toBuilder() {
			return newBuilder(this);
		}

		protected CommonProto.ReqRegister.Builder newBuilderForType(BuilderParent parent) {
			CommonProto.ReqRegister.Builder builder = new CommonProto.ReqRegister.Builder(parent);
			return builder;
		}

		static {
			defaultInstance.initFields();
		}

		public static final class Builder extends com.google.protobuf.GeneratedMessage.Builder<CommonProto.ReqRegister.Builder> implements CommonProto.ReqRegisterOrBuilder {
			private int bitField0_;
			private long serverId_;
			private int serverType_;
			private ByteString serverIP_;
			private int serverPort_;

			public static final Descriptor getDescriptor() {
				return CommonProto.internal_static_proto_ReqRegister_descriptor;
			}

			protected FieldAccessorTable internalGetFieldAccessorTable() {
				return CommonProto.internal_static_proto_ReqRegister_fieldAccessorTable.ensureFieldAccessorsInitialized(CommonProto.ReqRegister.class, CommonProto.ReqRegister.Builder.class);
			}

			private Builder() {
				this.serverIP_ = ByteString.EMPTY;
				this.maybeForceBuilderInitialization();
			}

			private Builder(BuilderParent parent) {
				super(parent);
				this.serverIP_ = ByteString.EMPTY;
				this.maybeForceBuilderInitialization();
			}

			private void maybeForceBuilderInitialization() {
				if (CommonProto.ReqRegister.alwaysUseFieldBuilders) {
				}

			}

			private static CommonProto.ReqRegister.Builder create() {
				return new CommonProto.ReqRegister.Builder();
			}

			public CommonProto.ReqRegister.Builder clear() {
				super.clear();
				this.serverId_ = 0L;
				this.bitField0_ &= -2;
				this.serverType_ = 0;
				this.bitField0_ &= -3;
				this.serverIP_ = ByteString.EMPTY;
				this.bitField0_ &= -5;
				this.serverPort_ = 0;
				this.bitField0_ &= -9;
				return this;
			}

			public CommonProto.ReqRegister.Builder clone() {
				return create().mergeFrom(this.buildPartial());
			}

			public Descriptor getDescriptorForType() {
				return CommonProto.internal_static_proto_ReqRegister_descriptor;
			}

			public CommonProto.ReqRegister getDefaultInstanceForType() {
				return CommonProto.ReqRegister.getDefaultInstance();
			}

			public CommonProto.ReqRegister build() {
				CommonProto.ReqRegister result = this.buildPartial();
				if (!result.isInitialized()) {
					throw newUninitializedMessageException(result);
				} else {
					return result;
				}
			}

			public CommonProto.ReqRegister buildPartial() {
				CommonProto.ReqRegister result = new CommonProto.ReqRegister(this);
				int from_bitField0_ = this.bitField0_;
				int to_bitField0_ = 0;
				if ((from_bitField0_ & 1) == 1) {
					to_bitField0_ |= 1;
				}

				result.serverId_ = this.serverId_;
				if ((from_bitField0_ & 2) == 2) {
					to_bitField0_ |= 2;
				}

				result.serverType_ = this.serverType_;
				if ((from_bitField0_ & 4) == 4) {
					to_bitField0_ |= 4;
				}

				result.serverIP_ = this.serverIP_;
				if ((from_bitField0_ & 8) == 8) {
					to_bitField0_ |= 8;
				}

				result.serverPort_ = this.serverPort_;
				result.bitField0_ = to_bitField0_;
				this.onBuilt();
				return result;
			}

			public CommonProto.ReqRegister.Builder mergeFrom(Message other) {
				if (other instanceof CommonProto.ReqRegister) {
					return this.mergeFrom((CommonProto.ReqRegister) other);
				} else {
					super.mergeFrom(other);
					return this;
				}
			}

			public CommonProto.ReqRegister.Builder mergeFrom(CommonProto.ReqRegister other) {
				if (other == CommonProto.ReqRegister.getDefaultInstance()) {
					return this;
				} else {
					if (other.hasServerId()) {
						this.setServerId(other.getServerId());
					}

					if (other.hasServerType()) {
						this.setServerType(other.getServerType());
					}

					if (other.hasServerIP()) {
						this.setServerIP(other.getServerIP());
					}

					if (other.hasServerPort()) {
						this.setServerPort(other.getServerPort());
					}

					this.mergeUnknownFields(other.getUnknownFields());
					return this;
				}
			}

			public final boolean isInitialized() {
				if (!this.hasServerId()) {
					return false;
				} else {
					return this.hasServerType();
				}
			}

			public CommonProto.ReqRegister.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
				CommonProto.ReqRegister parsedMessage = null;

				try {
					parsedMessage = (CommonProto.ReqRegister) CommonProto.ReqRegister.PARSER.parsePartialFrom(input, extensionRegistry);
				} catch (InvalidProtocolBufferException var8) {
					parsedMessage = (CommonProto.ReqRegister) var8.getUnfinishedMessage();
					throw var8;
				} finally {
					if (parsedMessage != null) {
						this.mergeFrom(parsedMessage);
					}

				}

				return this;
			}

			public boolean hasServerId() {
				return (this.bitField0_ & 1) == 1;
			}

			public long getServerId() {
				return this.serverId_;
			}

			public CommonProto.ReqRegister.Builder setServerId(long value) {
				this.bitField0_ |= 1;
				this.serverId_ = value;
				this.onChanged();
				return this;
			}

			public CommonProto.ReqRegister.Builder clearServerId() {
				this.bitField0_ &= -2;
				this.serverId_ = 0L;
				this.onChanged();
				return this;
			}

			public boolean hasServerType() {
				return (this.bitField0_ & 2) == 2;
			}

			public int getServerType() {
				return this.serverType_;
			}

			public CommonProto.ReqRegister.Builder setServerType(int value) {
				this.bitField0_ |= 2;
				this.serverType_ = value;
				this.onChanged();
				return this;
			}

			public CommonProto.ReqRegister.Builder clearServerType() {
				this.bitField0_ &= -3;
				this.serverType_ = 0;
				this.onChanged();
				return this;
			}

			public boolean hasServerIP() {
				return (this.bitField0_ & 4) == 4;
			}

			public ByteString getServerIP() {
				return this.serverIP_;
			}

			public CommonProto.ReqRegister.Builder setServerIP(ByteString value) {
				if (value == null) {
					throw new NullPointerException();
				} else {
					this.bitField0_ |= 4;
					this.serverIP_ = value;
					this.onChanged();
					return this;
				}
			}

			public CommonProto.ReqRegister.Builder clearServerIP() {
				this.bitField0_ &= -5;
				this.serverIP_ = CommonProto.ReqRegister.getDefaultInstance().getServerIP();
				this.onChanged();
				return this;
			}

			public boolean hasServerPort() {
				return (this.bitField0_ & 8) == 8;
			}

			public int getServerPort() {
				return this.serverPort_;
			}

			public CommonProto.ReqRegister.Builder setServerPort(int value) {
				this.bitField0_ |= 8;
				this.serverPort_ = value;
				this.onChanged();
				return this;
			}

			public CommonProto.ReqRegister.Builder clearServerPort() {
				this.bitField0_ &= -9;
				this.serverPort_ = 0;
				this.onChanged();
				return this;
			}
		}
	}

	public interface ReqRegisterOrBuilder extends MessageOrBuilder {
		boolean hasServerId();

		long getServerId();

		boolean hasServerType();

		int getServerType();

		boolean hasServerIP();

		ByteString getServerIP();

		boolean hasServerPort();

		int getServerPort();
	}

	public static final class Heartbeat extends GeneratedMessage implements CommonProto.HeartbeatOrBuilder {
		private static final CommonProto.Heartbeat defaultInstance = new CommonProto.Heartbeat(true);
		private final UnknownFieldSet unknownFields;
		public static Parser<CommonProto.Heartbeat> PARSER = new AbstractParser<CommonProto.Heartbeat>() {
			public CommonProto.Heartbeat parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
				return new CommonProto.Heartbeat(input, extensionRegistry);
			}
		};
		private int bitField0_;
		public static final int TIMES_FIELD_NUMBER = 1;
		private long times_;
		private byte memoizedIsInitialized;
		private int memoizedSerializedSize;
		private static final long serialVersionUID = 0L;

		private Heartbeat(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
			super(builder);
			this.memoizedIsInitialized = -1;
			this.memoizedSerializedSize = -1;
			this.unknownFields = builder.getUnknownFields();
		}

		private Heartbeat(boolean noInit) {
			this.memoizedIsInitialized = -1;
			this.memoizedSerializedSize = -1;
			this.unknownFields = UnknownFieldSet.getDefaultInstance();
		}

		public static CommonProto.Heartbeat getDefaultInstance() {
			return defaultInstance;
		}

		public CommonProto.Heartbeat getDefaultInstanceForType() {
			return defaultInstance;
		}

		public final UnknownFieldSet getUnknownFields() {
			return this.unknownFields;
		}

		private Heartbeat(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
			this.memoizedIsInitialized = -1;
			this.memoizedSerializedSize = -1;
			this.initFields();
			com.google.protobuf.UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

			try {
				boolean done = false;

				while (!done) {
					int tag = input.readTag();
					switch (tag) {
						case 0:
							done = true;
							break;
						case 8:
							this.bitField0_ |= 1;
							this.times_ = input.readInt64();
							break;
						default:
							if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
								done = true;
							}
					}
				}
			} catch (InvalidProtocolBufferException var11) {
				throw var11.setUnfinishedMessage(this);
			} catch (IOException var12) {
				throw (new InvalidProtocolBufferException(var12.getMessage())).setUnfinishedMessage(this);
			} finally {
				this.unknownFields = unknownFields.build();
				this.makeExtensionsImmutable();
			}

		}

		public static final Descriptor getDescriptor() {
			return CommonProto.internal_static_proto_Heartbeat_descriptor;
		}

		protected FieldAccessorTable internalGetFieldAccessorTable() {
			return CommonProto.internal_static_proto_Heartbeat_fieldAccessorTable.ensureFieldAccessorsInitialized(CommonProto.Heartbeat.class, CommonProto.Heartbeat.Builder.class);
		}

		public Parser<CommonProto.Heartbeat> getParserForType() {
			return PARSER;
		}

		public boolean hasTimes() {
			return (this.bitField0_ & 1) == 1;
		}

		public long getTimes() {
			return this.times_;
		}

		private void initFields() {
			this.times_ = 0L;
		}

		public final boolean isInitialized() {
			byte isInitialized = this.memoizedIsInitialized;
			if (isInitialized != -1) {
				return isInitialized == 1;
			} else {
				this.memoizedIsInitialized = 1;
				return true;
			}
		}

		public void writeTo(CodedOutputStream output) throws IOException {
			this.getSerializedSize();
			if ((this.bitField0_ & 1) == 1) {
				output.writeInt64(1, this.times_);
			}

			this.getUnknownFields().writeTo(output);
		}

		public int getSerializedSize() {
			int size = this.memoizedSerializedSize;
			if (size != -1) {
				return size;
			} else {
				size = 0;
				if ((this.bitField0_ & 1) == 1) {
					size += CodedOutputStream.computeInt64Size(1, this.times_);
				}

				size += this.getUnknownFields().getSerializedSize();
				this.memoizedSerializedSize = size;
				return size;
			}
		}

		protected Object writeReplace() throws ObjectStreamException {
			return super.writeReplace();
		}

		public static CommonProto.Heartbeat parseFrom(ByteString data) throws InvalidProtocolBufferException {
			return (CommonProto.Heartbeat) PARSER.parseFrom(data);
		}

		public static CommonProto.Heartbeat parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
			return (CommonProto.Heartbeat) PARSER.parseFrom(data, extensionRegistry);
		}

		public static CommonProto.Heartbeat parseFrom(byte[] data) throws InvalidProtocolBufferException {
			return (CommonProto.Heartbeat) PARSER.parseFrom(data);
		}

		public static CommonProto.Heartbeat parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
			return (CommonProto.Heartbeat) PARSER.parseFrom(data, extensionRegistry);
		}

		public static CommonProto.Heartbeat parseFrom(InputStream input) throws IOException {
			return (CommonProto.Heartbeat) PARSER.parseFrom(input);
		}

		public static CommonProto.Heartbeat parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
			return (CommonProto.Heartbeat) PARSER.parseFrom(input, extensionRegistry);
		}

		public static CommonProto.Heartbeat parseDelimitedFrom(InputStream input) throws IOException {
			return (CommonProto.Heartbeat) PARSER.parseDelimitedFrom(input);
		}

		public static CommonProto.Heartbeat parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
			return (CommonProto.Heartbeat) PARSER.parseDelimitedFrom(input, extensionRegistry);
		}

		public static CommonProto.Heartbeat parseFrom(CodedInputStream input) throws IOException {
			return (CommonProto.Heartbeat) PARSER.parseFrom(input);
		}

		public static CommonProto.Heartbeat parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
			return (CommonProto.Heartbeat) PARSER.parseFrom(input, extensionRegistry);
		}

		public static CommonProto.Heartbeat.Builder newBuilder() {
			return CommonProto.Heartbeat.Builder.create();
		}

		public CommonProto.Heartbeat.Builder newBuilderForType() {
			return newBuilder();
		}

		public static CommonProto.Heartbeat.Builder newBuilder(CommonProto.Heartbeat prototype) {
			return newBuilder().mergeFrom(prototype);
		}

		public CommonProto.Heartbeat.Builder toBuilder() {
			return newBuilder(this);
		}

		protected CommonProto.Heartbeat.Builder newBuilderForType(BuilderParent parent) {
			CommonProto.Heartbeat.Builder builder = new CommonProto.Heartbeat.Builder(parent);
			return builder;
		}

		static {
			defaultInstance.initFields();
		}

		public static final class Builder extends com.google.protobuf.GeneratedMessage.Builder<CommonProto.Heartbeat.Builder> implements CommonProto.HeartbeatOrBuilder {
			private int bitField0_;
			private long times_;

			public static final Descriptor getDescriptor() {
				return CommonProto.internal_static_proto_Heartbeat_descriptor;
			}

			protected FieldAccessorTable internalGetFieldAccessorTable() {
				return CommonProto.internal_static_proto_Heartbeat_fieldAccessorTable.ensureFieldAccessorsInitialized(CommonProto.Heartbeat.class, CommonProto.Heartbeat.Builder.class);
			}

			private Builder() {
				this.maybeForceBuilderInitialization();
			}

			private Builder(BuilderParent parent) {
				super(parent);
				this.maybeForceBuilderInitialization();
			}

			private void maybeForceBuilderInitialization() {
				if (CommonProto.Heartbeat.alwaysUseFieldBuilders) {
				}

			}

			private static CommonProto.Heartbeat.Builder create() {
				return new CommonProto.Heartbeat.Builder();
			}

			public CommonProto.Heartbeat.Builder clear() {
				super.clear();
				this.times_ = 0L;
				this.bitField0_ &= -2;
				return this;
			}

			public CommonProto.Heartbeat.Builder clone() {
				return create().mergeFrom(this.buildPartial());
			}

			public Descriptor getDescriptorForType() {
				return CommonProto.internal_static_proto_Heartbeat_descriptor;
			}

			public CommonProto.Heartbeat getDefaultInstanceForType() {
				return CommonProto.Heartbeat.getDefaultInstance();
			}

			public CommonProto.Heartbeat build() {
				CommonProto.Heartbeat result = this.buildPartial();
				if (!result.isInitialized()) {
					throw newUninitializedMessageException(result);
				} else {
					return result;
				}
			}

			public CommonProto.Heartbeat buildPartial() {
				CommonProto.Heartbeat result = new CommonProto.Heartbeat(this);
				int from_bitField0_ = this.bitField0_;
				int to_bitField0_ = 0;
				if ((from_bitField0_ & 1) == 1) {
					to_bitField0_ |= 1;
				}

				result.times_ = this.times_;
				result.bitField0_ = to_bitField0_;
				this.onBuilt();
				return result;
			}

			public CommonProto.Heartbeat.Builder mergeFrom(Message other) {
				if (other instanceof CommonProto.Heartbeat) {
					return this.mergeFrom((CommonProto.Heartbeat) other);
				} else {
					super.mergeFrom(other);
					return this;
				}
			}

			public CommonProto.Heartbeat.Builder mergeFrom(CommonProto.Heartbeat other) {
				if (other == CommonProto.Heartbeat.getDefaultInstance()) {
					return this;
				} else {
					if (other.hasTimes()) {
						this.setTimes(other.getTimes());
					}

					this.mergeUnknownFields(other.getUnknownFields());
					return this;
				}
			}

			public final boolean isInitialized() {
				return true;
			}

			public CommonProto.Heartbeat.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
				CommonProto.Heartbeat parsedMessage = null;

				try {
					parsedMessage = (CommonProto.Heartbeat) CommonProto.Heartbeat.PARSER.parsePartialFrom(input, extensionRegistry);
				} catch (InvalidProtocolBufferException var8) {
					parsedMessage = (CommonProto.Heartbeat) var8.getUnfinishedMessage();
					throw var8;
				} finally {
					if (parsedMessage != null) {
						this.mergeFrom(parsedMessage);
					}

				}

				return this;
			}

			public boolean hasTimes() {
				return (this.bitField0_ & 1) == 1;
			}

			public long getTimes() {
				return this.times_;
			}

			public CommonProto.Heartbeat.Builder setTimes(long value) {
				this.bitField0_ |= 1;
				this.times_ = value;
				this.onChanged();
				return this;
			}

			public CommonProto.Heartbeat.Builder clearTimes() {
				this.bitField0_ &= -2;
				this.times_ = 0L;
				this.onChanged();
				return this;
			}
		}
	}

	public interface HeartbeatOrBuilder extends MessageOrBuilder {
		boolean hasTimes();

		long getTimes();
	}

	public static final class KStrVPair extends GeneratedMessage implements CommonProto.KStrVPairOrBuilder {
		private static final CommonProto.KStrVPair defaultInstance = new CommonProto.KStrVPair(true);
		private final UnknownFieldSet unknownFields;
		public static Parser<CommonProto.KStrVPair> PARSER = new AbstractParser<CommonProto.KStrVPair>() {
			public CommonProto.KStrVPair parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
				return new CommonProto.KStrVPair(input, extensionRegistry);
			}
		};
		private int bitField0_;
		public static final int KEY_FIELD_NUMBER = 1;
		private long key_;
		public static final int VALUE_FIELD_NUMBER = 2;
		private ByteString value_;
		private byte memoizedIsInitialized;
		private int memoizedSerializedSize;
		private static final long serialVersionUID = 0L;

		private KStrVPair(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
			super(builder);
			this.memoizedIsInitialized = -1;
			this.memoizedSerializedSize = -1;
			this.unknownFields = builder.getUnknownFields();
		}

		private KStrVPair(boolean noInit) {
			this.memoizedIsInitialized = -1;
			this.memoizedSerializedSize = -1;
			this.unknownFields = UnknownFieldSet.getDefaultInstance();
		}

		public static CommonProto.KStrVPair getDefaultInstance() {
			return defaultInstance;
		}

		public CommonProto.KStrVPair getDefaultInstanceForType() {
			return defaultInstance;
		}

		public final UnknownFieldSet getUnknownFields() {
			return this.unknownFields;
		}

		private KStrVPair(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
			this.memoizedIsInitialized = -1;
			this.memoizedSerializedSize = -1;
			this.initFields();
			com.google.protobuf.UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

			try {
				boolean done = false;

				while (!done) {
					int tag = input.readTag();
					switch (tag) {
						case 0:
							done = true;
							break;
						case 8:
							this.bitField0_ |= 1;
							this.key_ = input.readInt64();
							break;
						case 18:
							this.bitField0_ |= 2;
							this.value_ = input.readBytes();
							break;
						default:
							if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
								done = true;
							}
					}
				}
			} catch (InvalidProtocolBufferException var11) {
				throw var11.setUnfinishedMessage(this);
			} catch (IOException var12) {
				throw (new InvalidProtocolBufferException(var12.getMessage())).setUnfinishedMessage(this);
			} finally {
				this.unknownFields = unknownFields.build();
				this.makeExtensionsImmutable();
			}

		}

		public static final Descriptor getDescriptor() {
			return CommonProto.internal_static_proto_KStrVPair_descriptor;
		}

		protected FieldAccessorTable internalGetFieldAccessorTable() {
			return CommonProto.internal_static_proto_KStrVPair_fieldAccessorTable.ensureFieldAccessorsInitialized(CommonProto.KStrVPair.class, CommonProto.KStrVPair.Builder.class);
		}

		public Parser<CommonProto.KStrVPair> getParserForType() {
			return PARSER;
		}

		public boolean hasKey() {
			return (this.bitField0_ & 1) == 1;
		}

		public long getKey() {
			return this.key_;
		}

		public boolean hasValue() {
			return (this.bitField0_ & 2) == 2;
		}

		public ByteString getValue() {
			return this.value_;
		}

		private void initFields() {
			this.key_ = 0L;
			this.value_ = ByteString.EMPTY;
		}

		public final boolean isInitialized() {
			byte isInitialized = this.memoizedIsInitialized;
			if (isInitialized != -1) {
				return isInitialized == 1;
			} else {
				this.memoizedIsInitialized = 1;
				return true;
			}
		}

		public void writeTo(CodedOutputStream output) throws IOException {
			this.getSerializedSize();
			if ((this.bitField0_ & 1) == 1) {
				output.writeInt64(1, this.key_);
			}

			if ((this.bitField0_ & 2) == 2) {
				output.writeBytes(2, this.value_);
			}

			this.getUnknownFields().writeTo(output);
		}

		public int getSerializedSize() {
			int size = this.memoizedSerializedSize;
			if (size != -1) {
				return size;
			} else {
				size = 0;
				if ((this.bitField0_ & 1) == 1) {
					size += CodedOutputStream.computeInt64Size(1, this.key_);
				}

				if ((this.bitField0_ & 2) == 2) {
					size += CodedOutputStream.computeBytesSize(2, this.value_);
				}

				size += this.getUnknownFields().getSerializedSize();
				this.memoizedSerializedSize = size;
				return size;
			}
		}

		protected Object writeReplace() throws ObjectStreamException {
			return super.writeReplace();
		}

		public static CommonProto.KStrVPair parseFrom(ByteString data) throws InvalidProtocolBufferException {
			return (CommonProto.KStrVPair) PARSER.parseFrom(data);
		}

		public static CommonProto.KStrVPair parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
			return (CommonProto.KStrVPair) PARSER.parseFrom(data, extensionRegistry);
		}

		public static CommonProto.KStrVPair parseFrom(byte[] data) throws InvalidProtocolBufferException {
			return (CommonProto.KStrVPair) PARSER.parseFrom(data);
		}

		public static CommonProto.KStrVPair parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
			return (CommonProto.KStrVPair) PARSER.parseFrom(data, extensionRegistry);
		}

		public static CommonProto.KStrVPair parseFrom(InputStream input) throws IOException {
			return (CommonProto.KStrVPair) PARSER.parseFrom(input);
		}

		public static CommonProto.KStrVPair parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
			return (CommonProto.KStrVPair) PARSER.parseFrom(input, extensionRegistry);
		}

		public static CommonProto.KStrVPair parseDelimitedFrom(InputStream input) throws IOException {
			return (CommonProto.KStrVPair) PARSER.parseDelimitedFrom(input);
		}

		public static CommonProto.KStrVPair parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
			return (CommonProto.KStrVPair) PARSER.parseDelimitedFrom(input, extensionRegistry);
		}

		public static CommonProto.KStrVPair parseFrom(CodedInputStream input) throws IOException {
			return (CommonProto.KStrVPair) PARSER.parseFrom(input);
		}

		public static CommonProto.KStrVPair parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
			return (CommonProto.KStrVPair) PARSER.parseFrom(input, extensionRegistry);
		}

		public static CommonProto.KStrVPair.Builder newBuilder() {
			return CommonProto.KStrVPair.Builder.create();
		}

		public CommonProto.KStrVPair.Builder newBuilderForType() {
			return newBuilder();
		}

		public static CommonProto.KStrVPair.Builder newBuilder(CommonProto.KStrVPair prototype) {
			return newBuilder().mergeFrom(prototype);
		}

		public CommonProto.KStrVPair.Builder toBuilder() {
			return newBuilder(this);
		}

		protected CommonProto.KStrVPair.Builder newBuilderForType(BuilderParent parent) {
			CommonProto.KStrVPair.Builder builder = new CommonProto.KStrVPair.Builder(parent);
			return builder;
		}

		static {
			defaultInstance.initFields();
		}

		public static final class Builder extends com.google.protobuf.GeneratedMessage.Builder<CommonProto.KStrVPair.Builder> implements CommonProto.KStrVPairOrBuilder {
			private int bitField0_;
			private long key_;
			private ByteString value_;

			public static final Descriptor getDescriptor() {
				return CommonProto.internal_static_proto_KStrVPair_descriptor;
			}

			protected FieldAccessorTable internalGetFieldAccessorTable() {
				return CommonProto.internal_static_proto_KStrVPair_fieldAccessorTable.ensureFieldAccessorsInitialized(CommonProto.KStrVPair.class, CommonProto.KStrVPair.Builder.class);
			}

			private Builder() {
				this.value_ = ByteString.EMPTY;
				this.maybeForceBuilderInitialization();
			}

			private Builder(BuilderParent parent) {
				super(parent);
				this.value_ = ByteString.EMPTY;
				this.maybeForceBuilderInitialization();
			}

			private void maybeForceBuilderInitialization() {
				if (CommonProto.KStrVPair.alwaysUseFieldBuilders) {
				}

			}

			private static CommonProto.KStrVPair.Builder create() {
				return new CommonProto.KStrVPair.Builder();
			}

			public CommonProto.KStrVPair.Builder clear() {
				super.clear();
				this.key_ = 0L;
				this.bitField0_ &= -2;
				this.value_ = ByteString.EMPTY;
				this.bitField0_ &= -3;
				return this;
			}

			public CommonProto.KStrVPair.Builder clone() {
				return create().mergeFrom(this.buildPartial());
			}

			public Descriptor getDescriptorForType() {
				return CommonProto.internal_static_proto_KStrVPair_descriptor;
			}

			public CommonProto.KStrVPair getDefaultInstanceForType() {
				return CommonProto.KStrVPair.getDefaultInstance();
			}

			public CommonProto.KStrVPair build() {
				CommonProto.KStrVPair result = this.buildPartial();
				if (!result.isInitialized()) {
					throw newUninitializedMessageException(result);
				} else {
					return result;
				}
			}

			public CommonProto.KStrVPair buildPartial() {
				CommonProto.KStrVPair result = new CommonProto.KStrVPair(this);
				int from_bitField0_ = this.bitField0_;
				int to_bitField0_ = 0;
				if ((from_bitField0_ & 1) == 1) {
					to_bitField0_ |= 1;
				}

				result.key_ = this.key_;
				if ((from_bitField0_ & 2) == 2) {
					to_bitField0_ |= 2;
				}

				result.value_ = this.value_;
				result.bitField0_ = to_bitField0_;
				this.onBuilt();
				return result;
			}

			public CommonProto.KStrVPair.Builder mergeFrom(Message other) {
				if (other instanceof CommonProto.KStrVPair) {
					return this.mergeFrom((CommonProto.KStrVPair) other);
				} else {
					super.mergeFrom(other);
					return this;
				}
			}

			public CommonProto.KStrVPair.Builder mergeFrom(CommonProto.KStrVPair other) {
				if (other == CommonProto.KStrVPair.getDefaultInstance()) {
					return this;
				} else {
					if (other.hasKey()) {
						this.setKey(other.getKey());
					}

					if (other.hasValue()) {
						this.setValue(other.getValue());
					}

					this.mergeUnknownFields(other.getUnknownFields());
					return this;
				}
			}

			public final boolean isInitialized() {
				return true;
			}

			public CommonProto.KStrVPair.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
				CommonProto.KStrVPair parsedMessage = null;

				try {
					parsedMessage = (CommonProto.KStrVPair) CommonProto.KStrVPair.PARSER.parsePartialFrom(input, extensionRegistry);
				} catch (InvalidProtocolBufferException var8) {
					parsedMessage = (CommonProto.KStrVPair) var8.getUnfinishedMessage();
					throw var8;
				} finally {
					if (parsedMessage != null) {
						this.mergeFrom(parsedMessage);
					}

				}

				return this;
			}

			public boolean hasKey() {
				return (this.bitField0_ & 1) == 1;
			}

			public long getKey() {
				return this.key_;
			}

			public CommonProto.KStrVPair.Builder setKey(long value) {
				this.bitField0_ |= 1;
				this.key_ = value;
				this.onChanged();
				return this;
			}

			public CommonProto.KStrVPair.Builder clearKey() {
				this.bitField0_ &= -2;
				this.key_ = 0L;
				this.onChanged();
				return this;
			}

			public boolean hasValue() {
				return (this.bitField0_ & 2) == 2;
			}

			public ByteString getValue() {
				return this.value_;
			}

			public CommonProto.KStrVPair.Builder setValue(ByteString value) {
				if (value == null) {
					throw new NullPointerException();
				} else {
					this.bitField0_ |= 2;
					this.value_ = value;
					this.onChanged();
					return this;
				}
			}

			public CommonProto.KStrVPair.Builder clearValue() {
				this.bitField0_ &= -3;
				this.value_ = CommonProto.KStrVPair.getDefaultInstance().getValue();
				this.onChanged();
				return this;
			}
		}
	}

	public interface KStrVPairOrBuilder extends MessageOrBuilder {
		boolean hasKey();

		long getKey();

		boolean hasValue();

		ByteString getValue();
	}

	public static final class KVPair extends GeneratedMessage implements CommonProto.KVPairOrBuilder {
		private static final CommonProto.KVPair defaultInstance = new CommonProto.KVPair(true);
		private final UnknownFieldSet unknownFields;
		public static Parser<CommonProto.KVPair> PARSER = new AbstractParser<CommonProto.KVPair>() {
			public CommonProto.KVPair parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
				return new CommonProto.KVPair(input, extensionRegistry);
			}
		};
		private int bitField0_;
		public static final int KEY_FIELD_NUMBER = 1;
		private long key_;
		public static final int VALUE_FIELD_NUMBER = 2;
		private long value_;
		private byte memoizedIsInitialized;
		private int memoizedSerializedSize;
		private static final long serialVersionUID = 0L;

		private KVPair(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
			super(builder);
			this.memoizedIsInitialized = -1;
			this.memoizedSerializedSize = -1;
			this.unknownFields = builder.getUnknownFields();
		}

		private KVPair(boolean noInit) {
			this.memoizedIsInitialized = -1;
			this.memoizedSerializedSize = -1;
			this.unknownFields = UnknownFieldSet.getDefaultInstance();
		}

		public static CommonProto.KVPair getDefaultInstance() {
			return defaultInstance;
		}

		public CommonProto.KVPair getDefaultInstanceForType() {
			return defaultInstance;
		}

		public final UnknownFieldSet getUnknownFields() {
			return this.unknownFields;
		}

		private KVPair(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
			this.memoizedIsInitialized = -1;
			this.memoizedSerializedSize = -1;
			this.initFields();
			com.google.protobuf.UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

			try {
				boolean done = false;

				while (!done) {
					int tag = input.readTag();
					switch (tag) {
						case 0:
							done = true;
							break;
						case 8:
							this.bitField0_ |= 1;
							this.key_ = input.readInt64();
							break;
						case 16:
							this.bitField0_ |= 2;
							this.value_ = input.readInt64();
							break;
						default:
							if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
								done = true;
							}
					}
				}
			} catch (InvalidProtocolBufferException var11) {
				throw var11.setUnfinishedMessage(this);
			} catch (IOException var12) {
				throw (new InvalidProtocolBufferException(var12.getMessage())).setUnfinishedMessage(this);
			} finally {
				this.unknownFields = unknownFields.build();
				this.makeExtensionsImmutable();
			}

		}

		public static final Descriptor getDescriptor() {
			return CommonProto.internal_static_proto_KVPair_descriptor;
		}

		protected FieldAccessorTable internalGetFieldAccessorTable() {
			return CommonProto.internal_static_proto_KVPair_fieldAccessorTable.ensureFieldAccessorsInitialized(CommonProto.KVPair.class, CommonProto.KVPair.Builder.class);
		}

		public Parser<CommonProto.KVPair> getParserForType() {
			return PARSER;
		}

		public boolean hasKey() {
			return (this.bitField0_ & 1) == 1;
		}

		public long getKey() {
			return this.key_;
		}

		public boolean hasValue() {
			return (this.bitField0_ & 2) == 2;
		}

		public long getValue() {
			return this.value_;
		}

		private void initFields() {
			this.key_ = 0L;
			this.value_ = 0L;
		}

		public final boolean isInitialized() {
			byte isInitialized = this.memoizedIsInitialized;
			if (isInitialized != -1) {
				return isInitialized == 1;
			} else {
				this.memoizedIsInitialized = 1;
				return true;
			}
		}

		public void writeTo(CodedOutputStream output) throws IOException {
			this.getSerializedSize();
			if ((this.bitField0_ & 1) == 1) {
				output.writeInt64(1, this.key_);
			}

			if ((this.bitField0_ & 2) == 2) {
				output.writeInt64(2, this.value_);
			}

			this.getUnknownFields().writeTo(output);
		}

		public int getSerializedSize() {
			int size = this.memoizedSerializedSize;
			if (size != -1) {
				return size;
			} else {
				size = 0;
				if ((this.bitField0_ & 1) == 1) {
					size += CodedOutputStream.computeInt64Size(1, this.key_);
				}

				if ((this.bitField0_ & 2) == 2) {
					size += CodedOutputStream.computeInt64Size(2, this.value_);
				}

				size += this.getUnknownFields().getSerializedSize();
				this.memoizedSerializedSize = size;
				return size;
			}
		}

		protected Object writeReplace() throws ObjectStreamException {
			return super.writeReplace();
		}

		public static CommonProto.KVPair parseFrom(ByteString data) throws InvalidProtocolBufferException {
			return (CommonProto.KVPair) PARSER.parseFrom(data);
		}

		public static CommonProto.KVPair parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
			return (CommonProto.KVPair) PARSER.parseFrom(data, extensionRegistry);
		}

		public static CommonProto.KVPair parseFrom(byte[] data) throws InvalidProtocolBufferException {
			return (CommonProto.KVPair) PARSER.parseFrom(data);
		}

		public static CommonProto.KVPair parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
			return (CommonProto.KVPair) PARSER.parseFrom(data, extensionRegistry);
		}

		public static CommonProto.KVPair parseFrom(InputStream input) throws IOException {
			return (CommonProto.KVPair) PARSER.parseFrom(input);
		}

		public static CommonProto.KVPair parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
			return (CommonProto.KVPair) PARSER.parseFrom(input, extensionRegistry);
		}

		public static CommonProto.KVPair parseDelimitedFrom(InputStream input) throws IOException {
			return (CommonProto.KVPair) PARSER.parseDelimitedFrom(input);
		}

		public static CommonProto.KVPair parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
			return (CommonProto.KVPair) PARSER.parseDelimitedFrom(input, extensionRegistry);
		}

		public static CommonProto.KVPair parseFrom(CodedInputStream input) throws IOException {
			return (CommonProto.KVPair) PARSER.parseFrom(input);
		}

		public static CommonProto.KVPair parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
			return (CommonProto.KVPair) PARSER.parseFrom(input, extensionRegistry);
		}

		public static CommonProto.KVPair.Builder newBuilder() {
			return CommonProto.KVPair.Builder.create();
		}

		public CommonProto.KVPair.Builder newBuilderForType() {
			return newBuilder();
		}

		public static CommonProto.KVPair.Builder newBuilder(CommonProto.KVPair prototype) {
			return newBuilder().mergeFrom(prototype);
		}

		public CommonProto.KVPair.Builder toBuilder() {
			return newBuilder(this);
		}

		protected CommonProto.KVPair.Builder newBuilderForType(BuilderParent parent) {
			CommonProto.KVPair.Builder builder = new CommonProto.KVPair.Builder(parent);
			return builder;
		}

		static {
			defaultInstance.initFields();
		}

		public static final class Builder extends com.google.protobuf.GeneratedMessage.Builder<CommonProto.KVPair.Builder> implements CommonProto.KVPairOrBuilder {
			private int bitField0_;
			private long key_;
			private long value_;

			public static final Descriptor getDescriptor() {
				return CommonProto.internal_static_proto_KVPair_descriptor;
			}

			protected FieldAccessorTable internalGetFieldAccessorTable() {
				return CommonProto.internal_static_proto_KVPair_fieldAccessorTable.ensureFieldAccessorsInitialized(CommonProto.KVPair.class, CommonProto.KVPair.Builder.class);
			}

			private Builder() {
				this.maybeForceBuilderInitialization();
			}

			private Builder(BuilderParent parent) {
				super(parent);
				this.maybeForceBuilderInitialization();
			}

			private void maybeForceBuilderInitialization() {
				if (CommonProto.KVPair.alwaysUseFieldBuilders) {
				}

			}

			private static CommonProto.KVPair.Builder create() {
				return new CommonProto.KVPair.Builder();
			}

			public CommonProto.KVPair.Builder clear() {
				super.clear();
				this.key_ = 0L;
				this.bitField0_ &= -2;
				this.value_ = 0L;
				this.bitField0_ &= -3;
				return this;
			}

			public CommonProto.KVPair.Builder clone() {
				return create().mergeFrom(this.buildPartial());
			}

			public Descriptor getDescriptorForType() {
				return CommonProto.internal_static_proto_KVPair_descriptor;
			}

			public CommonProto.KVPair getDefaultInstanceForType() {
				return CommonProto.KVPair.getDefaultInstance();
			}

			public CommonProto.KVPair build() {
				CommonProto.KVPair result = this.buildPartial();
				if (!result.isInitialized()) {
					throw newUninitializedMessageException(result);
				} else {
					return result;
				}
			}

			public CommonProto.KVPair buildPartial() {
				CommonProto.KVPair result = new CommonProto.KVPair(this);
				int from_bitField0_ = this.bitField0_;
				int to_bitField0_ = 0;
				if ((from_bitField0_ & 1) == 1) {
					to_bitField0_ |= 1;
				}

				result.key_ = this.key_;
				if ((from_bitField0_ & 2) == 2) {
					to_bitField0_ |= 2;
				}

				result.value_ = this.value_;
				result.bitField0_ = to_bitField0_;
				this.onBuilt();
				return result;
			}

			public CommonProto.KVPair.Builder mergeFrom(Message other) {
				if (other instanceof CommonProto.KVPair) {
					return this.mergeFrom((CommonProto.KVPair) other);
				} else {
					super.mergeFrom(other);
					return this;
				}
			}

			public CommonProto.KVPair.Builder mergeFrom(CommonProto.KVPair other) {
				if (other == CommonProto.KVPair.getDefaultInstance()) {
					return this;
				} else {
					if (other.hasKey()) {
						this.setKey(other.getKey());
					}

					if (other.hasValue()) {
						this.setValue(other.getValue());
					}

					this.mergeUnknownFields(other.getUnknownFields());
					return this;
				}
			}

			public final boolean isInitialized() {
				return true;
			}

			public CommonProto.KVPair.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
				CommonProto.KVPair parsedMessage = null;

				try {
					parsedMessage = (CommonProto.KVPair) CommonProto.KVPair.PARSER.parsePartialFrom(input, extensionRegistry);
				} catch (InvalidProtocolBufferException var8) {
					parsedMessage = (CommonProto.KVPair) var8.getUnfinishedMessage();
					throw var8;
				} finally {
					if (parsedMessage != null) {
						this.mergeFrom(parsedMessage);
					}

				}

				return this;
			}

			public boolean hasKey() {
				return (this.bitField0_ & 1) == 1;
			}

			public long getKey() {
				return this.key_;
			}

			public CommonProto.KVPair.Builder setKey(long value) {
				this.bitField0_ |= 1;
				this.key_ = value;
				this.onChanged();
				return this;
			}

			public CommonProto.KVPair.Builder clearKey() {
				this.bitField0_ &= -2;
				this.key_ = 0L;
				this.onChanged();
				return this;
			}

			public boolean hasValue() {
				return (this.bitField0_ & 2) == 2;
			}

			public long getValue() {
				return this.value_;
			}

			public CommonProto.KVPair.Builder setValue(long value) {
				this.bitField0_ |= 2;
				this.value_ = value;
				this.onChanged();
				return this;
			}

			public CommonProto.KVPair.Builder clearValue() {
				this.bitField0_ &= -3;
				this.value_ = 0L;
				this.onChanged();
				return this;
			}
		}
	}

	public interface KVPairOrBuilder extends MessageOrBuilder {
		boolean hasKey();

		long getKey();

		boolean hasValue();

		long getValue();
	}
}
