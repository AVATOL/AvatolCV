echo "running launchTest"
echo %1
for /f "delims=" %%A in (%1) do set %%A
echo "running step 1" > %avatolCVStatusFile%
echo "running step 1"
ping 127.0.0.1 -n 6

echo "running step 2"
echo "running step 2" > %avatolCVStatusFile%
ping 127.0.0.1 -n 6

echo "running step 3"
echo "running step 3" > %avatolCVStatusFile%
ping 127.0.0.1 -n 6

echo "running step 4"
echo "running step 4" > %avatolCVStatusFile%
ping 127.0.0.1 -n 6

echo "running step 5"
echo "running step 5" > %avatolCVStatusFile%
ping 127.0.0.1 -n 6

echo "running step 6"
echo "running step 6" > %avatolCVStatusFile%
