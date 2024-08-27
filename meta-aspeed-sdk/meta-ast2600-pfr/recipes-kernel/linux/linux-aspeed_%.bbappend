FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:ast2600-dcscm = " \
	file://mctp.cfg \
        file://aspeed-ast2600-dcscm-mctp-socket.dts \
"

do_prepare_dts() {
    for DTB in ${KERNEL_DEVICETREE}; do
        DT=`basename ${DTB} .dtb`
        if [ -r "${WORKDIR}/${DT}.dts" ]; then
            cp ${WORKDIR}/${DT}.dts \
                ${STAGING_KERNEL_DIR}/arch/${ARCH}/boot/dts/aspeed/
        fi
    done
}

addtask prepare_dts before do_configure after do_set_local_version
