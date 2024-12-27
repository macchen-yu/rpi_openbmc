SUMMARY = "Synchronization Zephyr Sample"
DESCRIPTION = "A simple Zephyr application that demonstrates basic sanity of \
the kernel. It demonstrates that kernel scheduling, communication and timing \
operate correctly by printing a greeting to the console from two threads."

include zephyr-sample.inc

ZEPHYR_SRC_DIR = "${ZEPHYR_BASE}/samples/synchronization"
