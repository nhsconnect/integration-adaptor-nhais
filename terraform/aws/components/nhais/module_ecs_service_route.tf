module "nhais_ecs_service" {
  source = "../../modules/module_ecs_service"

  project = var.project
  component = var.component
  module_instance = "nhais_ecs_service"

  cluster_id = data.terraform_remote_state.base.outputs.cluster_id
  desired_count = var.nhais_service_desired_count
  container_port = var.nhais_service_container_port
  launch_type = var.nhais_service_launch_type

  vpc_id = data.terraform_remote_state.base.outputs.vpc_id
  subnet_cidrs = [
    cidrsubnet(data.terraform_remote_state.base.outputs.vpc_cidr,3,1),
    cidrsubnet(data.terraform_remote_state.base.outputs.vpc_cidr,3,2),
    cidrsubnet(data.terraform_remote_state.base.outputs.vpc_cidr,3,3)
  ]

}