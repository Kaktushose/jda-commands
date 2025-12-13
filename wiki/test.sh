if grep -Eq 'WARNING|ERROR' out.log; then
  echo "Found warnings or errors:"
  grep -E 'WARNING|ERROR' out.log
  exit 1
fi