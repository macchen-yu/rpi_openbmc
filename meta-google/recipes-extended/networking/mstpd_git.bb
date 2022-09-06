PR = "r1"
PV = "0.1+git${SRCPV}"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4325afd396febcb659c36b49533135d4"

SRC_URI = "git://github.com/mstpd/mstpd;branch=master;protocol=https"
SRCREV = "181c453fc1a00573e19f14960dcc54ad84beea7c"
S = "${WORKDIR}/git"

SRC_URI += "file://bridge-stp"
SRC_URI += "file://mstpd.service"

inherit autotools pkgconfig systemd

PACKAGES =+ "${PN}-mstpd"
FILES:${PN}-mstpd = "${sbindir}/mstpd ${sbindir}/mstpctl /sbin/bridge-stp"

SYSTEMD_PACKAGES = "${PN}-mstpd"
SYSTEMD_SERVICE:${PN}-mstpd = "mstpd.service"

do_install:append() {
  rm -r ${D}${libexecdir} ${D}${libdir}/NetworkManager
  rmdir ${D}${libdir}

  install -d -m 0755 ${D}/sbin
  install -m 0755 ${WORKDIR}/bridge-stp ${D}/sbin

  install -d -m 0755 ${D}${systemd_system_unitdir}
  install -m 0644 ${WORKDIR}/mstpd.service ${D}${systemd_system_unitdir}/
}
