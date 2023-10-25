package com.example.launcher;

public class GrpcServer {
    private static final String TAG = "GrpcServer";
    private static final int PORT = 50051;
    private Server server;

    public void start() throws IOException {
        server = ServerBuilder.forPort(PORT)
                .addService(new LauncherService())
                .build()
                .start();
        Log.i(TAG, "Server started, listening on " + PORT);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                Log.i(TAG, "*** shutting down gRPC server since JVM is shutting down");
                GrpcServer.this.stop();
                Log.i(TAG, "*** server shut down");
            }
        });
    }

    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
}
