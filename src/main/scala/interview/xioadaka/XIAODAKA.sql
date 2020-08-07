SELECT
    tb_habit.open_id,
    COUNT(DISTINCT(tb_habit.open_id)) habit_cnt,
    COUNT(DISTINCT(CASE WHEN user_habit_relation.open_id IS NOT NULL THEN user_habit_relation.open_id ELSE NULL END)) user_cnt
FROM tb_habit
left outer join user_habit_relation ON (tb_habit.habit_id=user_habit_relation.habit_id)
GROUP BY tb_habit.open_id