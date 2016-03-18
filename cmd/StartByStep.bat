@setlocal enabledelayedexpansion
@set classpath=bin
@for %%c in (lib\*.jar) do @set classpath=!classpath!;%%c
@set classpath=%classpath%;./bin;
@set java_heap_max=-Xmx512m


java %java_heap_max% -cp %classpath% com.crawler.start.FetchByStep %MODELS%
exit