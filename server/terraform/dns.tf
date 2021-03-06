resource "google_dns_managed_zone" "coursecollider-com" {
  name = "coursecollider-com"
  dns_name = "coursecollider.com."
}

resource "google_dns_record_set" "app" {
  name = "app.${google_dns_managed_zone.coursecollider-com.dns_name}"
  type = "A"
  ttl  = 60

  managed_zone = "${google_dns_managed_zone.coursecollider-com.name}"

  rrdatas = ["${google_compute_address.tenagra-ip.address}"]
}
