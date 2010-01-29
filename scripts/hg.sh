#!/bin/sh

REPO_DIR=/usr/hg/wrttools/
LOG_DIR=/var/log/mercurial 
HG_CMD=/usr/local/bin/hg

cd $REPO_DIR
${HG_CMD} serve -d -A ${LOG_DIR}/accesslog$$ -E ${LOG_DIR}/errorlog$$ 
# ${HG_CMD} serve
