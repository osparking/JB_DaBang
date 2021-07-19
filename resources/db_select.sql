-- 고객 제공 관련 주소록 질의어
select 단.도로명주소, 고.상세주소, 단.관리번호 
from jb_dabang.고객주소 고
join 고객단지 단 on 단.단지번호 = 고.단지번호 
where 고.고객ID = 4;