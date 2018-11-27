provider "google" {
  credentials = "${file("${var.credentials_path}")}"
  project     = "${var.project_id}"
  region      = "${var.region}"
  version     = "~> 1.19"
}

data "google_compute_image" "os" {
  family = "ubuntu-1804-lts"
  project = "ubuntu-os-cloud"
}

resource "google_compute_instance" "instance" {
  name = "tenagra"
  machine_type = "n1-standard-1"
  zone = "${var.zone}"
  tags = ["http-server", "swarm"]

  boot_disk {
    initialize_params {
      image = "${data.google_compute_image.os.self_link}"
    }
  }

  network_interface {
  network = "default"

  access_config {
    // Ephemeral IP
    }
  }

  metadata {
    ssh-keys = "root:${file("${var.public_key_path}")}"
    startup-script = "${file("bootstrap.sh")}"
  }
}

resource "google_compute_firewall" "http_ingress" {
  name = "http-server-firewall"
  network = "default"
  
  allow {
    protocol = "tcp"
    ports = ["80", "443"]
  }

  source_ranges = ["0.0.0.0/0"]
  target_tags = ["http-server"]
}

output "tenagra-ip" {
  value = "${google_compute_instance.instance.network_interface.0.access_config.0.nat_ip}"
}
