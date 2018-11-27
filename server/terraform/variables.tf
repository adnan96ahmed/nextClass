variable "project_id" {
    default = "coursecollider"
}

variable "region" {
    default = "us-central1"
}

variable "zone" {
    default = "us-central1-a"
}

variable "credentials_path" {
    default = "~/.gcloud/terraform-credentials.json"
}

variable "public_key_path" {
    default = "~/.ssh/gcloud_id_ed25519.pub"
}
