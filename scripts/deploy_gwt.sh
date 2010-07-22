# Simple script to deploy compiled GWT code into the AndWellness
# Server codebase for deployment

COMPILED_SUBDIR=war/andwellnessvisualizations
GWT_SUBDIR=web/js/gwt

EXPECTED_ARGS=1
E_BADARGS=65

if [ $# -ne $EXPECTED_ARGS ]
then
  echo "Simple script to deploy compiled GWT code into the AndWellness Server codebase for deployment"
  echo "Usage: `basename $0` <andwellness server base dir>"
  exit $E_BADARGS
fi


cp ${COMPILED_SUBDIR}/*js $1/${GWT_SUBDIR}
cp ${COMPILED_SUBDIR}/*cache* $1/${GWT_SUBDIR}
