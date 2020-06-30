--
-- Created by IntelliJ IDEA.
-- User: liaomengge
-- Date: 17/1/10
-- Time: 上午11:15
-- To change this template use File | Settings | File Templates.
--

local current = redis.call("incr", KEYS[1])

if tonumber(current) == 1
then redis.call("expire", KEYS[1], ARGV[1])
end