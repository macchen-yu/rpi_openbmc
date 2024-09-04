require recipes-kernel/zephyr-kernel/zephyr-image.inc
require zephyr-aspeed-irot-src.inc

PV = "1.0+git"

# zephyr
SRCREV_zephyr = "${AUTOREV}"
ZEPHYR_BRANCH = "aspeed-dev-v3.7.0"

# aspeed-irot
SRCREV_aspeed-irot = "${AUTOREV}"
ASPEED_ZEPHYR_PROJECT_BRANCH = "aspeed-irot"

ZEPHYR_SRC_DIR = "${S}/aspeed-zephyr-project/apps/aspeed-irot"
