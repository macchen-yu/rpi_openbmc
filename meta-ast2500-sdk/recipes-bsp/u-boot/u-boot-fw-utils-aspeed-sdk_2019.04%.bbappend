FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

ENV_CONFIG_FILE = "fw_env.config"
SRC_URI_append = " file://fw_env.config"

do_install_append () {
	install -m 644 ${WORKDIR}/${ENV_CONFIG_FILE} ${D}${sysconfdir}/fw_env.config
}

