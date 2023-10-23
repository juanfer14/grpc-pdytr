package pdytr.grpc;

import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;

/**
 * <pre>
 * Defining a Service, a Service can have multiple RPC operations
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.7.0)",
    comments = "Source: FtpService.proto")
public final class FtpServiceGrpc {

  private FtpServiceGrpc() {}

  public static final String SERVICE_NAME = "pdytr.grpc.FtpService";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<pdytr.grpc.FtpServiceOuterClass.FtpRequestRead,
      pdytr.grpc.FtpServiceOuterClass.FtpResponseRead> METHOD_READ =
      io.grpc.MethodDescriptor.<pdytr.grpc.FtpServiceOuterClass.FtpRequestRead, pdytr.grpc.FtpServiceOuterClass.FtpResponseRead>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
          .setFullMethodName(generateFullMethodName(
              "pdytr.grpc.FtpService", "read"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              pdytr.grpc.FtpServiceOuterClass.FtpRequestRead.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              pdytr.grpc.FtpServiceOuterClass.FtpResponseRead.getDefaultInstance()))
          .setSchemaDescriptor(new FtpServiceMethodDescriptorSupplier("read"))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<pdytr.grpc.FtpServiceOuterClass.WriteRequest,
      pdytr.grpc.FtpServiceOuterClass.WriteResponse> METHOD_WRITE =
      io.grpc.MethodDescriptor.<pdytr.grpc.FtpServiceOuterClass.WriteRequest, pdytr.grpc.FtpServiceOuterClass.WriteResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "pdytr.grpc.FtpService", "write"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              pdytr.grpc.FtpServiceOuterClass.WriteRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              pdytr.grpc.FtpServiceOuterClass.WriteResponse.getDefaultInstance()))
          .setSchemaDescriptor(new FtpServiceMethodDescriptorSupplier("write"))
          .build();

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static FtpServiceStub newStub(io.grpc.Channel channel) {
    return new FtpServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static FtpServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new FtpServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static FtpServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new FtpServiceFutureStub(channel);
  }

  /**
   * <pre>
   * Defining a Service, a Service can have multiple RPC operations
   * </pre>
   */
  public static abstract class FtpServiceImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     * Define a RPC operation
     *rpc greeting(HelloRequest) returns (stream HelloResponse);
     * </pre>
     */
    public void read(pdytr.grpc.FtpServiceOuterClass.FtpRequestRead request,
        io.grpc.stub.StreamObserver<pdytr.grpc.FtpServiceOuterClass.FtpResponseRead> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_READ, responseObserver);
    }

    /**
     */
    public void write(pdytr.grpc.FtpServiceOuterClass.WriteRequest request,
        io.grpc.stub.StreamObserver<pdytr.grpc.FtpServiceOuterClass.WriteResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_WRITE, responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_READ,
            asyncServerStreamingCall(
              new MethodHandlers<
                pdytr.grpc.FtpServiceOuterClass.FtpRequestRead,
                pdytr.grpc.FtpServiceOuterClass.FtpResponseRead>(
                  this, METHODID_READ)))
          .addMethod(
            METHOD_WRITE,
            asyncUnaryCall(
              new MethodHandlers<
                pdytr.grpc.FtpServiceOuterClass.WriteRequest,
                pdytr.grpc.FtpServiceOuterClass.WriteResponse>(
                  this, METHODID_WRITE)))
          .build();
    }
  }

  /**
   * <pre>
   * Defining a Service, a Service can have multiple RPC operations
   * </pre>
   */
  public static final class FtpServiceStub extends io.grpc.stub.AbstractStub<FtpServiceStub> {
    private FtpServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private FtpServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FtpServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new FtpServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Define a RPC operation
     *rpc greeting(HelloRequest) returns (stream HelloResponse);
     * </pre>
     */
    public void read(pdytr.grpc.FtpServiceOuterClass.FtpRequestRead request,
        io.grpc.stub.StreamObserver<pdytr.grpc.FtpServiceOuterClass.FtpResponseRead> responseObserver) {
      asyncServerStreamingCall(
          getChannel().newCall(METHOD_READ, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void write(pdytr.grpc.FtpServiceOuterClass.WriteRequest request,
        io.grpc.stub.StreamObserver<pdytr.grpc.FtpServiceOuterClass.WriteResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_WRITE, getCallOptions()), request, responseObserver);
    }
  }

  /**
   * <pre>
   * Defining a Service, a Service can have multiple RPC operations
   * </pre>
   */
  public static final class FtpServiceBlockingStub extends io.grpc.stub.AbstractStub<FtpServiceBlockingStub> {
    private FtpServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private FtpServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FtpServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new FtpServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Define a RPC operation
     *rpc greeting(HelloRequest) returns (stream HelloResponse);
     * </pre>
     */
    public java.util.Iterator<pdytr.grpc.FtpServiceOuterClass.FtpResponseRead> read(
        pdytr.grpc.FtpServiceOuterClass.FtpRequestRead request) {
      return blockingServerStreamingCall(
          getChannel(), METHOD_READ, getCallOptions(), request);
    }

    /**
     */
    public pdytr.grpc.FtpServiceOuterClass.WriteResponse write(pdytr.grpc.FtpServiceOuterClass.WriteRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_WRITE, getCallOptions(), request);
    }
  }

  /**
   * <pre>
   * Defining a Service, a Service can have multiple RPC operations
   * </pre>
   */
  public static final class FtpServiceFutureStub extends io.grpc.stub.AbstractStub<FtpServiceFutureStub> {
    private FtpServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private FtpServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FtpServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new FtpServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<pdytr.grpc.FtpServiceOuterClass.WriteResponse> write(
        pdytr.grpc.FtpServiceOuterClass.WriteRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_WRITE, getCallOptions()), request);
    }
  }

  private static final int METHODID_READ = 0;
  private static final int METHODID_WRITE = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final FtpServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(FtpServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_READ:
          serviceImpl.read((pdytr.grpc.FtpServiceOuterClass.FtpRequestRead) request,
              (io.grpc.stub.StreamObserver<pdytr.grpc.FtpServiceOuterClass.FtpResponseRead>) responseObserver);
          break;
        case METHODID_WRITE:
          serviceImpl.write((pdytr.grpc.FtpServiceOuterClass.WriteRequest) request,
              (io.grpc.stub.StreamObserver<pdytr.grpc.FtpServiceOuterClass.WriteResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class FtpServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    FtpServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return pdytr.grpc.FtpServiceOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("FtpService");
    }
  }

  private static final class FtpServiceFileDescriptorSupplier
      extends FtpServiceBaseDescriptorSupplier {
    FtpServiceFileDescriptorSupplier() {}
  }

  private static final class FtpServiceMethodDescriptorSupplier
      extends FtpServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    FtpServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (FtpServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new FtpServiceFileDescriptorSupplier())
              .addMethod(METHOD_READ)
              .addMethod(METHOD_WRITE)
              .build();
        }
      }
    }
    return result;
  }
}
