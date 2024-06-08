import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoFileRepository;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;
import ru.netology.patient.service.alert.SendAlertServiceImpl;
import ru.netology.patient.service.medical.MedicalService;
import ru.netology.patient.service.medical.MedicalServiceImpl;

import java.math.BigDecimal;

public class MedicalServiceTest {
    /*

     */

    @ParameterizedTest
    @CsvSource(value = {"13, 120, 100, 1"})
    void testCheckBloodPressure(String idPatient, int highInt, int lowInt, int times) {
        //регистрация кол-ва вызовов alertService.send(message) и его содержимое для пациента с плохим давлением
        //times - кол-во вызовов alertService.send(message) для пациента.

        PatientInfo patientInfo = Mockito.mock(PatientInfo.class);

        SendAlertService sendAlertService = Mockito.mock(SendAlertServiceImpl.class);

        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(patientInfoRepository.getById(idPatient)).thenReturn(patientInfo);

        BloodPressure bloodPressureExpected = new BloodPressure(100, 80);
        BloodPressure bloodPressureActually = new BloodPressure(highInt, lowInt);
//.thenReturn(new .... ) - приводит к ошибке
//должно быть правильное расположение thenReturn


        HealthInfo healthInfo = Mockito.mock(HealthInfo.class);
        Mockito.when(healthInfo.getBloodPressure()).thenReturn(bloodPressureActually);

        Mockito.when(patientInfo.getHealthInfo()).thenReturn(healthInfo);
        Mockito.when(patientInfo.getId()).thenReturn(idPatient);

        MedicalService medicalService = new MedicalServiceImpl(patientInfoRepository, sendAlertService);
        medicalService.checkBloodPressure(idPatient, bloodPressureExpected);

        String message = String.format("Warning, patient with id: %s, need help", patientInfo.getId());

        Mockito.verify(sendAlertService, Mockito.times(times)).send(message);

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(sendAlertService).send(argumentCaptor.capture());
        Assertions.assertEquals("Warning, patient with id: 13, need help", argumentCaptor.getValue());





    }

    @ParameterizedTest
    @CsvSource(value = {"777, 36.6, 0", "13, 43.14, 1"})
    void testСheckTemperature(String idPatient, BigDecimal temperature, int times) {
        //регистрация вызовов alertService.send(message) для пациентов с нормальной и ненормальной температурами соответственно
        //первый пациент - нормально вызова не будет, где times - кол-во вызовов


        PatientInfo patientInfo = Mockito.mock(PatientInfo.class);

        SendAlertService sendAlertService = Mockito.mock(SendAlertServiceImpl.class);

        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(patientInfoRepository.getById(idPatient)).thenReturn(patientInfo);

        HealthInfo healthInfo = Mockito.mock(HealthInfo.class);
        Mockito.when(healthInfo.getNormalTemperature()).thenReturn(temperature);

        Mockito.when(patientInfo.getHealthInfo()).thenReturn(healthInfo);
        Mockito.when(patientInfo.getId()).thenReturn(idPatient);

        BigDecimal normalTemperature = new BigDecimal(36.3);

        MedicalService medicalService = new MedicalServiceImpl(patientInfoRepository, sendAlertService);
        medicalService.checkTemperature(idPatient, normalTemperature);

        String message = String.format("Warning, patient with id: %s, need help", patientInfo.getId());

        Mockito.verify(sendAlertService, Mockito.times(times)).send(message);


    }

    @ParameterizedTest
    @CsvSource(value = {"777, 36.6, 100, 80"})
    void testСheckNormal(String idPatient, BigDecimal temperature, int highInt, int lowInt) {
        //обобщенные показатели в норме - сообщений нет 


        PatientInfo patientInfo = Mockito.mock(PatientInfo.class);

        SendAlertService sendAlertService = Mockito.mock(SendAlertServiceImpl.class);

        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(patientInfoRepository.getById(idPatient)).thenReturn(patientInfo);

        BloodPressure bloodPressureExpected = new BloodPressure(100, 80);
        BloodPressure bloodPressureActually = new BloodPressure(highInt, lowInt);

        HealthInfo healthInfo = Mockito.mock(HealthInfo.class);
        Mockito.when(healthInfo.getNormalTemperature()).thenReturn(temperature);
        Mockito.when(healthInfo.getBloodPressure()).thenReturn(bloodPressureActually);

        Mockito.when(patientInfo.getHealthInfo()).thenReturn(healthInfo);
        Mockito.when(patientInfo.getId()).thenReturn(idPatient);

        BigDecimal normalTemperature = new BigDecimal(36.3);

        MedicalService medicalService = new MedicalServiceImpl(patientInfoRepository, sendAlertService);
        medicalService.checkTemperature(idPatient, normalTemperature);
        medicalService.checkBloodPressure(idPatient, bloodPressureExpected);

        String message = String.format("Warning, patient with id: %s, need help", patientInfo.getId());

        Mockito.verify(sendAlertService, Mockito.times(0)).send(message);


    }





}
