resource "aws_appautoscaling_target" "service_autoscaling_target" {
  count = var.enable_load_balancing ? 1 : 0
  max_capacity = var.desired_count * 2
  min_capacity = var.minimal_count
  resource_id = "service/${var.cluster_name}/${aws_ecs_service.ecs_service.name}"
  role_arn = var.task_scaling_role_arn
  scalable_dimension = "ecs:service:DesiredCount"
  service_namespace = "ecs"
}