variable "account_id" {
  type = string
  description = "ID of AWS Account on which the resources are created"
}

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
  default = "nhais"
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

# variable "nhais_image_name" {
#   type = string
#   description = "Path to docker image to be used in task definition"
# }

variable "build_id" {
  type = string
  description = "Number of the current build, used for tagging the logs"
}

variable "mq_broker_name" {
  type = string
  description = "Name of the MQ broker shared between all envs"
}

variable "environment_variables" {
  type = list(object({name=string, value=string}))
  description = "List of objects for Environment variables"
  default = []
}

variable "docdb_master_user" {
  type = string
  description = "Username for Document DB master user"
  default = "master_user"
}

variable "docdb_master_password" {
  type = string
  description = "Password for Document DB master user"
  default = "ChangeMe"
}

variable "nhais_log_level" {
  type = string
  description = "Level of logging for NHAIS application"
  default = "INFO"
}

variable "nhais_healthcheck_path" {
  type = string
  description = "Path on which the container provides info about its status"
  default = "/healthcheck"
}

variable "docdb_tls" {
  type = string
  default = "disabled"
  description = "Should the Document DB have a TLS enabled for incomming connections"
}

variable "docdb_audit_logs" {
  type = string
  default = "disabled"
  description = "Should audit logs be enabled for Document DB"
}

variable "docdb_retention_period" {
  type = number
  description = "How many days should the backups be kept, default is 1 day"
  default = 1
}

variable "docdb_instance_class" {
  type = string
  description = "Instance size to be used to Document DB instances"
}

variable "nhais_amqp_max_retries" {
  type = number
  description = "Max retries on connection to amqp"
  default = 3
}

variable "nhais_amqp_retry_delay" {
  type = number
  description = "Delay on retries to connect to amqp"
  default = 100
}