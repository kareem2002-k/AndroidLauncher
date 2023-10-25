package main

import (
	"context"
	"log"
	"net"

	pb "example.com/go-launcher/packs"
	"google.golang.org/grpc"
)

type serv struct {
	pb.UnimplementedPackageServiceServer
}

func (s *serv) GetPackageNames(ctx context.Context, in *pb.PackageRequest) (*pb.PackageResponse, error) {
	userRole := in.GetUserRole()

	if userRole == "admin" {
		return &pb.PackageResponse{PackagesNames: []string{"com.example.app1", "com.example.app2"}}, nil
	} else if userRole == "user" {
		return &pb.PackageResponse{PackagesNames: []string{"com.example.app1"}}, nil
	} else {
		return &pb.PackageResponse{PackagesNames: []string{}}, nil
	}
}

func main() {
	lis, err := net.Listen("tcp", ":50051") // Define your server address and port
	if err != nil {
		log.Fatalf("Failed to listen: %v", err)
	}

	grpcServer := grpc.NewServer()
	pb.RegisterPackageServiceServer(grpcServer, &serv{})

	if err := grpcServer.Serve(lis); err != nil {
		log.Fatalf("Failed to serve: %v", err)
	}
}
