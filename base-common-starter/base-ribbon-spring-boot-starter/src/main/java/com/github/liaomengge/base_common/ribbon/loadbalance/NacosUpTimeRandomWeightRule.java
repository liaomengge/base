package com.github.liaomengge.base_common.ribbon.loadbalance;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.github.liaomengge.base_common.nacos.consts.NacosConst;
import com.github.liaomengge.base_common.utils.date.LyJdk8DateUtil;
import com.github.liaomengge.base_common.utils.number.LyBigDecimalUtil;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by liaomengge on 2020/12/28.
 */
public class NacosUpTimeRandomWeightRule extends NacosRandomWeightRule {

    @Override
    public Instance chooseInstance(List<Instance> instances) {
        instances = rebuildInstanceWeight(instances);
        return super.chooseInstance(instances);
    }

    protected List<Instance> rebuildInstanceWeight(List<Instance> instances) {
        if (instances.size() == 1) {
            return instances;
        }
        return instances.stream().map(instance -> {
            instance.setWeight(getWeight(instance));
            return instance;
        }).collect(Collectors.toList());
    }

    protected double getWeight(Instance instance) {
        double weight = instance.getWeight();
        String registerTime = instance.getMetadata().get(NacosConst.MetadataConst.PRESERVED_REGISTER_TIME);
        if (StringUtils.isBlank(registerTime)) {
            return weight;
        }
        LocalDateTime registerLocalDateTime = LyJdk8DateUtil.getString2Date(registerTime);
        long startUpTime = ChronoUnit.MILLIS.between(registerLocalDateTime, LocalDateTime.now());
        startUpTime = Math.max(startUpTime, 1);
        long warmupTime = ribbonProperties.getWeightWarmup().getTime().toMillis();
        if (startUpTime < warmupTime) {
            weight = calculateWarmupWeight(startUpTime, warmupTime, weight);
        }
        return weight;
    }

    private double calculateWarmupWeight(long startUpTime, long warmupTime, double weight) {
        double calculateWeight = LyBigDecimalUtil.div(LyBigDecimalUtil.mul(startUpTime, weight), warmupTime);
        return Math.min(Math.max(calculateWeight, Double.valueOf(0.01d)), weight);
    }
}
