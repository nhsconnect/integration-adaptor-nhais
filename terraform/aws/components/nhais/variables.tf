variable "project" {
  type = string
  description = "Name of the project where this code is used"
}

variable "environment" {
  type = string
  description = "Name of the environment"
}

variable "component" {
  type = string
  description = "Name of the component"
  default = "base"
}

variable "region" {
  type = string
  description = "Region where the resources will be created"
}

variable "base_cidr_block" {
  type = string
  description = "CIDR block to use for VPC"
}

variable "tf_state_bucket" {
  type = string
  description = "Name of S3 bucket with TF state of components"
}

variable "nhais_service_desired_count" {
  type = number
  description = "Number of containers to run in the service"
}

variable "nhais_service_container_port" {
  type = number
  description = "Port Number on which service within container will be listening"
}

variable "nhais_service_launch_type" {
  type = string
  description = "Type of cluster on which this service will be run, FARGATE or EC2"
}