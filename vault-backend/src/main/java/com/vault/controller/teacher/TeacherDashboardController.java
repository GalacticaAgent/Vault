package com.vault.controller.teacher;

import com.vault.common.Result;
import com.vault.dto.request.CreateCardRequest;
import com.vault.dto.response.TeacherDashboardResponse;
import com.vault.security.JwtUtil;
import com.vault.service.teacher.TeacherDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
public class TeacherDashboardController {

    private final TeacherDashboardService teacherDashboardService;
    private final JwtUtil jwtUtil;

    @GetMapping("/dashboard")
    public Result<TeacherDashboardResponse> getDashboard(
            @RequestHeader(name = "Authorization") String authorization,
            @RequestParam(name = "className", required = false) String className
    ) {
        String token = authorization.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);
        if (!"TEACHER".equalsIgnoreCase(role)) {
            return Result.error(403, "仅教师可访问该接口");
        }
        String cn = StringUtils.hasText(className) ? className : null;
        TeacherDashboardResponse data = teacherDashboardService.getDashboard(userId, cn);
        return Result.success("获取成功", data);
    }

    @org.springframework.web.bind.annotation.PostMapping("/dashboard/card")
    public Result<Long> addCard(
            @RequestHeader(name = "Authorization") String authorization,
            @RequestBody CreateCardRequest request
    ) {
        String token = authorization.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);
        if (!"TEACHER".equalsIgnoreCase(role)) {
            return Result.error(403, "仅教师可访问该接口");
        }
        Long id = teacherDashboardService.addCard(userId, request);
        return Result.success("创建成功", id);
    }

    @GetMapping("/dashboard/cards")
    public Result<java.util.List<com.vault.entity.mysql.DashboardCard>> listCards(
            @RequestHeader(name = "Authorization") String authorization
    ) {
        String token = authorization.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);
        if (!"TEACHER".equalsIgnoreCase(role)) {
            return Result.error(403, "仅教师可访问该接口");
        }
        java.util.List<com.vault.entity.mysql.DashboardCard> cards = teacherDashboardService.listCards(userId);
        return Result.success("获取成功", cards);
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/dashboard/card/{id}")
    public Result<Boolean> deleteCard(
            @RequestHeader(name = "Authorization") String authorization,
            @org.springframework.web.bind.annotation.PathVariable("id") Long id
    ) {
        String token = authorization.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);
        if (!"TEACHER".equalsIgnoreCase(role)) {
            return Result.error(403, "仅教师可访问该接口");
        }
        boolean ok = teacherDashboardService.deleteCard(userId, id);
        return ok ? Result.success("删除成功", true) : Result.error(404, "卡片不存在或无权限");
    }

    @PostMapping("/dashboard/card/{id}/refresh")
    public Result<com.vault.entity.mysql.DashboardCard> refreshCard(
            @RequestHeader(name = "Authorization") String authorization,
            @org.springframework.web.bind.annotation.PathVariable("id") Long id
    ) {
        String token = authorization.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);
        if (!"TEACHER".equalsIgnoreCase(role)) {
            return Result.error(403, "仅教师可访问该接口");
        }
        com.vault.entity.mysql.DashboardCard card = teacherDashboardService.refreshCard(userId, id);
        if (card == null) {
            return Result.error(404, "卡片不存在或无权限");
        }
        return Result.success("刷新成功", card);
    }

    @GetMapping("/students/{id}/detail")
    public Result<com.vault.dto.response.TeacherStudentDetailResponse> getStudentDetail(
            @RequestHeader(name = "Authorization") String authorization,
            @PathVariable("id") Long studentId
    ) {
        String token = authorization.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);
        if (!"TEACHER".equals(role)) {
            return Result.error(403, "仅教师可访问该接口");
        }
        com.vault.dto.response.TeacherStudentDetailResponse detail = teacherDashboardService.getStudentDetail(userId, studentId);
        if (detail == null) {
            return Result.error(404, "学生不存在");
        }
        return Result.success("获取成功", detail);
    }

    @GetMapping("/classes/compare")
    public Result<java.util.List<com.vault.dto.response.TeacherDashboardResponse.ClassItem>> compareClasses(
            @RequestHeader(name = "Authorization") String authorization,
            @RequestParam(name = "classNames") String classNames
    ) {
        String token = authorization.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);
        if (!"TEACHER".equals(role)) {
            return Result.error(403, "仅教师可访问该接口");
        }
        java.util.List<String> names = java.util.Arrays.stream(classNames.split(","))
                .map(String::trim).filter(s -> !s.isEmpty()).toList();
        java.util.List<com.vault.dto.response.TeacherDashboardResponse.ClassItem> items = teacherDashboardService.compareClasses(userId, names);
        return Result.success("获取成功", items);
    }
}

