package com.travel.users.apis.valiadator;

import com.travel.common.utils.PhoneUtil;
import org.springframework.util.StringUtils;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.function.Predicate;

/**
 * @auther luo
 * @date 2019/11/9
 */
public class CheckMobileValidator implements ConstraintValidator<CheckMobile, Long> {


    private boolean require = false;

    @Override
    public void initialize(CheckMobile constraintAnnotation) {
        require = constraintAnnotation.required();

    }

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintValidatorContext) {
        Predicate<String> predicateM = (String value)-> PhoneUtil.checkPhone(value);
        if(require){
            return predicateM.test(id.toString());
        }else {
            Predicate<String> predicate = (String value)-> StringUtils.isEmpty(value);
            boolean result = predicate.test(id.toString());
            if(!result){
                return predicateM.test(id.toString());
            }
            return result;
        }
    }
}
