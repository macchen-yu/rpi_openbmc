FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " \
    file://0001-mctp-req-Add-data-argument-in-usage.patch \
    file://0002-mctpd-pfr-Support-intel-pfr-DAA-flow.patch \
    "

do_install:append() {
   install -m 755 ${WORKDIR}/build/mctp-req ${D}${bindir}
   install -m 755 ${WORKDIR}/build/mctp-echo ${D}${bindir}
}
