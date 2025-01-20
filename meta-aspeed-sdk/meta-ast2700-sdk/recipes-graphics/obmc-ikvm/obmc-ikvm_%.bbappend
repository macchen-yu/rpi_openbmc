FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

SRC_URI:append = " file://start-ipkvm1.service"
SRC_URI:append = " file://create_usbhid.sh"
SRC_URI:append = " file://0004-obmc-ikvm-support-ast2750-dual-nodes.patch"
SRC_URI:append = " ${@bb.utils.contains('MACHINE_FEATURES', 'ast2700-a0', \
                    '', 'file://0005-obmc-ikvm-support-ast2700-A1.patch', d)}"

SYSTEMD_SERVICE:${PN}:append = " start-ipkvm1.service"

FILES:${PN}:append = " \
    ${systemd_system_unitdir}/start-ipkvm1.service \
    ${bindir}/create_usbhid.sh \
"

do_install:append () {
    install -d ${D}${bindir} ${D}${systemd_system_unitdir}

    install -D -m 0644 ${WORKDIR}/start-ipkvm1.service ${D}${systemd_system_unitdir}
    install -D -m 0755 ${WORKDIR}/create_usbhid.sh ${D}${bindir}
}
