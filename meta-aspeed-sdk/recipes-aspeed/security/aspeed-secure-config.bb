SUMMARY = "ASPEED insecure keys for testing and development."
DESCRIPTION = "Do not use these keys to sign images."
PR = "r0"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch

SRC_URI = " \
    file://configs;subdir=${S} \
    file://keys;subdir=${S} \
    "

do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${datadir}
    install -d ${D}${datadir}/aspeed-secure-config
    install -d ${D}${datadir}/aspeed-secure-config/keys
    install -d ${D}${datadir}/aspeed-secure-config/ast2600
    install -d ${D}${datadir}/aspeed-secure-config/ast2600/otp
    install -d ${D}${datadir}/aspeed-secure-config/ast2600/data

    install -m 0755 ${S}/configs/*.sh \
        ${D}${datadir}/aspeed-secure-config
    install -m 0644 ${S}/keys/* \
        ${D}${datadir}/aspeed-secure-config/keys
    install -m 0644 ${S}/configs/ast2600/otp/* \
        ${D}${datadir}/aspeed-secure-config/ast2600/otp
    install -m 0644 ${S}/configs/ast2600/data/* \
        ${D}${datadir}/aspeed-secure-config/ast2600/data
}

BBCLASSEXTEND = "native"
