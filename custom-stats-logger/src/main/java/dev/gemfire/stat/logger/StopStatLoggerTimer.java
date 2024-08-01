package dev.gemfire.stat.logger;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.logging.internal.log4j.api.LogService;
import org.apache.logging.log4j.Logger;

import java.util.Timer;

public class StopStatLoggerTimer implements Function {
  private static final Logger logger = LogService.getLogger();

  @Override
  public boolean isHA() {
    return false;
  }

  @Override
  public boolean hasResult() {
    return false;
  }

  @Override
  public String getId() {
    return StopStatLoggerTimer.class.getSimpleName();
  }

  @Override
  public void execute(FunctionContext functionContext) {
    Cache cache = functionContext.getCache();
    Region<Object, Object> timerRegion = cache.getRegion("Timer");
    if (timerRegion != null) {
      Object statsLoggerTimer = timerRegion.get("statsLoggerTimer");
      if (statsLoggerTimer != null) {
        ((Timer) statsLoggerTimer).cancel();
        logger.info("Stopped statsLoggerTimer");
        timerRegion.destroyRegion();
        logger.info("Destroy Timer region");
        }
    }
  }
}
