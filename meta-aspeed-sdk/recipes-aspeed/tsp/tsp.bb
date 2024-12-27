SUMMARY = "The Tertiary Service Processor (TSP) firmware"
DESCRIPTION = "The Tertiary Service Processor (TSP) is \
ARM Cortex-M4F processor as third service processor(r0p1) for AST2700."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${ASPEEDSDKBASE}/LICENSE;md5=a3740bd0a194cd6dcafdc482a200a56f"
PACKAGE_ARCH = "${MACHINE_ARCH}"

PR = "r0"

TSP_FIRMWARE ?= "ast2700-tsp.bin"

SRC_URI = "file://${TSP_FIRMWARE};subdir=${S}"

do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"

inherit deploy

do_deploy () {
    install -d ${DEPLOYDIR}
    install -m 644 ${S}/${TSP_FIRMWARE} ${DEPLOYDIR}/.
}

addtask deploy before do_build after do_compile

ALLOW_EMPTY:${PN} = "1"

